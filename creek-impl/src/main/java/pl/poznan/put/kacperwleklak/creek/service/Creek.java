package pl.poznan.put.kacperwleklak.creek.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.common.utils.CollectionUtils;
import pl.poznan.put.kacperwleklak.common.utils.CommonPrefixResult;
import pl.poznan.put.kacperwleklak.creek.interfaces.CreekClient;
import pl.poznan.put.kacperwleklak.creek.interfaces.OperationExecutor;
import pl.poznan.put.kacperwleklak.creek.postgres.PostgresServer;
import pl.poznan.put.kacperwleklak.creek.protocol.EventID;
import pl.poznan.put.kacperwleklak.creek.protocol.Operation;
import pl.poznan.put.kacperwleklak.creek.protocol.Request;
import pl.poznan.put.kacperwleklak.creek.structure.response.Response;
import pl.poznan.put.kacperwleklak.creek.structure.response.ResponseHandler;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@DependsOn({"messageUtils"})
public class Creek implements ReliableChannelDeliverListener, CabDeliverListener, CabPredicate, OperationExecutor {

    private static final int PREDICATE_ID = 1;
    private static final String CREEK_PROTOCOL = "CREEK_PROTOCOL";

    // current state variables
    private int currentEventNumber;
    private final int replicaId;
    private final Set<EventID> casualCtx;
    private List<Request> tentative;
    private List<Request> committed;
    private List<Request> executed;
    private List<Request> toBeExecuted;
    private List<Request> toBeRolledBack;
    private final Map<Request, ResponseHandler> reqsAwaitingResp;
    private final Set<Request> missingContextOps;
    private final StateObject state;
    private final HashMap<EventID, CabPredicateCallback> callbackMap;

    // dependencies
    private final CAB cab;
    private final ReliableChannel reliableChannel;

    private final double cabProbability;

    private final Server pgServer;

    @Autowired
    public Creek(CAB cab, ReliableChannel reliableChannel, @Value("${postgres.port}") String pgPort,
                 @Value("${communication.replicas.id}") int replicaId, @Value("${cab.probability}") double cabProbability) throws SQLException {
        this.currentEventNumber = 0;
        this.casualCtx = new HashSet<>();
        this.tentative = new ArrayList<>();
        this.committed = new ArrayList<>();
        this.executed = new ArrayList<>();
        this.toBeExecuted = new ArrayList<>();
        this.toBeRolledBack = new ArrayList<>();
        this.reqsAwaitingResp = new HashMap<>();
        this.missingContextOps = new HashSet<>();
        this.callbackMap = new HashMap<>();
        this.replicaId = replicaId;

        this.cab = cab;
        this.reliableChannel = reliableChannel;
        PostgresServer postgresServer = new PostgresServer(this);
        this.pgServer = new Server(postgresServer, "-baseDir", "./", "-pgAllowOthers", "-ifNotExists", "-pgPort", pgPort);
        this.state = new StateObjectSql(postgresServer);
        this.cabProbability = cabProbability;
    }

    @PostConstruct
    public void postInitialization() throws SQLException {
        reliableChannel.registerListener(this);
        cab.registerListener(this);
        cab.start(Map.of(PREDICATE_ID, this));
        pgServer.start();
    }

    @Override
    public void executeOperation(Operation operation, CreekClient client) {
        invoke(operation, isStrong(operation), client);
    }

    private boolean isStrong(Operation operation) {
        //TODO: remove
//        String sqlString = operation.getSql().toLowerCase(Locale.ROOT);
//        return sqlString.matches("^.*call\\s+buy_now.*$") ||
//                sqlString.matches("^.*insert\\s+into\\s+users.*$");
        return ThreadLocalRandom.current().nextDouble() < cabProbability;
    }

    // upon invoke(op : ops(F), strongOp : boolean), Alg I, l. 15
    public void invoke(Operation operation, boolean isStrong, CreekClient client) {
        long start = System.currentTimeMillis();
        synchronized (this) {
            currentEventNumber++;
            EventID eventID = new EventID(Integer.valueOf(replicaId).byteValue(), currentEventNumber);
            Request request = new Request(getCurrentTime(), eventID, operation, isStrong);
            if (isStrong) {
                List<EventID> tentativeGreaterThanCurrentRequest = tentative
                        .stream()
                        .filter(request1 -> request1.compareTo(request) > 0)
                        .map(Request::getRequestID)
                        .collect(Collectors.toList());
                request.setCasualCtx(CollectionUtils.differenceToSet(casualCtx, tentativeGreaterThanCurrentRequest));
                cab.cabCast(request.getRequestID().toCabMessageId(), 1);
            }
            reqsAwaitingResp.put(request, new ResponseHandler(client));
            casualCtx.add(eventID);
            insertIntoTentative(request);
            broadcast(request);
        }
        long finish = System.currentTimeMillis();
        log.debug("Insert and Execute operation took {} ms", finish - start);
    }

    @Override
    public boolean testSync(CabMessageID messageID) {
        return checkDep(EventID.fromCabMessageId(messageID));
    }

    @Override
    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
        EventID eventID = EventID.fromCabMessageId(messageID);
        boolean test = checkDep(eventID);
        if (!test) {
            log.debug("Async tested false, saving callback");
            callbackMap.putIfAbsent(eventID, predicateCallback);
            return false;
        }
        return true;
    }

    // upon RB-deliver(r : Req)
    public void operationRequestHandler(Request request) {
        synchronized (this) {
            log.debug("Creek: OperationRequestHandler {}", request.getRequestID());
            if (request.getRequestID().getReplica() == (byte) replicaId) {
                log.debug("Creek: Operation request from myself, skipping");
                checkWaitingPredicates();
                return;
            }
            if (!request.isStrong() || casualCtx.containsAll(request.getCasualCtx())) {
                casualCtx.add(request.getRequestID());
                List<Request> readyToScheduleOps = new ArrayList<>(List.of(request));
                readyToScheduleOps = establishReadyToScheduleOps(readyToScheduleOps);
                insertIntoTentative(readyToScheduleOps);
            } else {
                missingContextOps.add(request);
            }
            checkWaitingPredicates();
        }
    }

    private void checkWaitingPredicates() {
        Iterator<Map.Entry<EventID, CabPredicateCallback>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<EventID, CabPredicateCallback> entry = iterator.next();
            EventID eventID = entry.getKey();
            boolean test = checkDep(eventID);
            if (test) {
                entry.getValue().predicateBecomesTrue(PREDICATE_ID, eventID.toCabMessageId());
                iterator.remove();
            }
        }
    }

    private List<Request> establishReadyToScheduleOps(List<Request> readyToScheduleOps) {
        boolean anythingUpdated = false;
        Iterator<Request> missingContextOpsIterator = missingContextOps.iterator();
        while (missingContextOpsIterator.hasNext()) {
            Request request = missingContextOpsIterator.next();
            if (casualCtx.containsAll(request.getCasualCtx())) {
                casualCtx.add(request.getRequestID());
                readyToScheduleOps.add(request);
                missingContextOpsIterator.remove();
                anythingUpdated = true;
            }
        }
        return anythingUpdated
                ? establishReadyToScheduleOps(readyToScheduleOps)
                : readyToScheduleOps;
    }

    // procedure commit(r : Req)
    private void commit(Request request) {
        List<Request> committedExt = tentative.stream()
                .filter(tentativeRequest -> request.getCasualCtx().contains(tentativeRequest.getRequestID()))
                .collect(Collectors.toList());
        List<Request> newTentative = tentative.stream()
                .filter(tentativeRequest -> !tentativeRequest.equals(request))
                .filter(tentativeRequest -> !committedExt.contains(tentativeRequest))
                .collect(Collectors.toList());
        committed.addAll(committedExt);
        committed.add(request);
        tentative = newTentative;
        List<Request> newOrder = CollectionUtils.concatLists(committed, tentative);
        adjustExecution(newOrder);
        List<Request> strongOpsToCheck = committedExt.stream()
                .filter(Request::isStrong)
                .collect(Collectors.toList());
        strongOpsToCheck.add(request);
        strongOpsToCheck.forEach(strongOpsToCheckRequest -> {
            ResponseHandler responseHandler = reqsAwaitingResp.get(strongOpsToCheckRequest);
            if (responseHandler != null && responseHandler.hasResponse() && executed.contains(strongOpsToCheckRequest)) {
                responseToClient(request, responseHandler);
                reqsAwaitingResp.remove(strongOpsToCheckRequest);
            }
        });
    }

    //upon CAB-deliver(id : pair〈int, int〉)
    @Override
    public void cabDelver(CabMessageID cabMessageID) {
        synchronized (this) {
            log.debug("CAB Delivered: {}", cabMessageID);
            tentative.stream()
                    .filter(request -> request.getRequestID().toCabMessageId().equals(cabMessageID))
                    .findAny()
                    .ifPresent(this::commit);
        }
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    private void broadcast(Request request) {
        try {
            reliableChannel.rCast(ThriftSerializer.serialize(request));
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoTentative(Request request) {
        insertIntoTentative(Collections.singletonList(request));
    }

    private void insertIntoTentative(List<Request> readyToScheduleOps) {
        tentative.addAll(readyToScheduleOps);
        tentative.sort(Request::compareTo);
        List<Request> newOrder = CollectionUtils.concatLists(committed, tentative);
        adjustExecution(newOrder);
    }

    private void adjustExecution(List<Request> newOrder) {
        synchronized (this) {
            CommonPrefixResult<Request> commonPrefixResult = CollectionUtils.longestCommonPrefix(executed, newOrder);
            List<Request> inOrder = commonPrefixResult.getCommonPrefix();
            List<Request> outOfOrder = commonPrefixResult.getFirstListTail();
            executed = inOrder;
            toBeExecuted = CollectionUtils.differenceToList(commonPrefixResult.getSecondListTail(), commonPrefixResult.getFirstListTail());
            Collections.reverse(outOfOrder);
            toBeRolledBack = CollectionUtils.concatLists(toBeRolledBack, outOfOrder);
            performExecutions();
        }
    }

    private boolean checkDep(EventID eventID) {
        synchronized (this) {
            return Stream.concat(committed.stream(), tentative.stream())
                    .filter(request -> eventID.equals(request.getRequestID()))
                    .findAny()
                    .map(request -> casualCtx.containsAll(request.getCasualCtx()))
                    .orElse(false);
        }
    }

    private void responseToClient(Request request, Response response) {
        ResponseHandler responseHandler = reqsAwaitingResp.get(request);
        responseHandler.setResponse(response);
        responseToClient(request, responseHandler);
    }

    private void responseToClient(Request request, ResponseHandler responseHandler) {
        responseHandler.sendResponse();
    }

    private ResponseHandler updateReqsAwaitingResponse(Request request, Response response) {
        ResponseHandler responseHandler = reqsAwaitingResp.get(request);
        responseHandler.setResponse(response);
        reqsAwaitingResp.put(request, responseHandler);
        return responseHandler;
    }


    private void performExecutions() {
        if (!toBeRolledBack.isEmpty()) {
            Iterator<Request> toBeRolledBackIterator = toBeRolledBack.iterator();
            while (toBeRolledBackIterator.hasNext()) {
                Request request = toBeRolledBackIterator.next();
                state.rollback(request);
                toBeRolledBackIterator.remove();
            }
        }
        if (toBeRolledBack.isEmpty() && !toBeExecuted.isEmpty()) {
            Iterator<Request> toBeExecutedIterator = toBeExecuted.iterator();
            while (toBeExecutedIterator.hasNext()) {
                Request request = toBeExecutedIterator.next();
                Response response = state.execute(request);
                if (reqsAwaitingResp.containsKey(request)) {
                    if (!request.isStrong()) {
                        responseToClient(request, response);
                        reqsAwaitingResp.remove(request);
                    } else if (tentative.contains(request)) {
                        updateReqsAwaitingResponse(request, response);
                        //responseToClient(request, responseHandler);
                    } else {
                        responseToClient(request, response);
                        reqsAwaitingResp.remove(request);
                    }
                }
                executed.add(request);
                toBeExecutedIterator.remove();
            }
        }
    }

    @Override
    public void rDeliver(byte msgType, byte[] msg) {
        try {
            if (msgType == (byte) 1) {
                Request request = new Request();
                ThriftSerializer.deserialize(request, msg);
                operationRequestHandler(request);
            }
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
