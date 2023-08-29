package pl.poznan.put.kacperwleklak.creek.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import pl.poznan.put.kacperwleklak.appcommon.db.OperationExecutor;
import pl.poznan.put.kacperwleklak.appcommon.db.PostgresServer;
import pl.poznan.put.kacperwleklak.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.appcommon.db.response.Response;
import pl.poznan.put.kacperwleklak.appcommon.db.response.ResponseHandler;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;
import pl.poznan.put.kacperwleklak.common.utils.CollectionUtils;
import pl.poznan.put.kacperwleklak.common.utils.CommonPrefixResult;
import pl.poznan.put.kacperwleklak.creek.interfaces.AllOpsDoneListener;
import pl.poznan.put.kacperwleklak.creek.protocol.Dot;
import pl.poznan.put.kacperwleklak.creek.protocol.DottedVersionVector;
import pl.poznan.put.kacperwleklak.creek.protocol.Operation;
import pl.poznan.put.kacperwleklak.creek.protocol.Request;
import pl.poznan.put.kacperwleklak.creek.utils.AppCommonConverter;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ConcurrentZMQChannelSupervisor;
import zmq.socket.reqrep.Req;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@DependsOn({"messageUtils"})
public class Creek implements CabDeliverListener, CabPredicate, OperationExecutor {

    public static final int PREDICATE_ID = 1;

    // current state variables
    private int currentEventNumber;
    private final int replicaId;
    private final DottedVersionVector causalCtx;
    private ArrayList<Request> tentative;
    private ArrayList<Request> committed;
    private List<Request> executed;
    private List<Request> toBeRolledBack;
    private final Map<Request, ResponseHandler> reqsAwaitingResp;
    private final Set<Request> missingContextOps;
    private final StateObjectAdapter state;
    private final ConcurrentHashMap<Dot, CabPredicateCallback> callbackMap;
    private final Map<Dot, Request> allRequests;
    private final Map<CabMessageID, Request> strongOpsInTentative;

    // dependencies
    private final CAB cab;
    private final ConcurrentZMQChannelSupervisor channelSupervisor;
    private final AllOpsDoneListener allOpsDoneListener;

    private final Object lock = new Object();

    @Autowired
    public Creek(CAB cab, ConcurrentZMQChannelSupervisor channelSupervisor, int replicaId, int noOfReplicas,
                 PostgresServer postgresServer, AllOpsDoneListener allOpsDoneListener) {
        this.currentEventNumber = 0;
        this.causalCtx = new DottedVersionVector(noOfReplicas);
        this.tentative = new ArrayList<>();
        this.committed = new ArrayList<>();
        this.executed = new ArrayList<>();
        this.toBeRolledBack = new LinkedList<>();
        this.reqsAwaitingResp = new HashMap<>();
        this.missingContextOps = new HashSet<>();
        this.callbackMap = new ConcurrentHashMap<>();
        this.replicaId = replicaId;
        this.allRequests = new HashMap<>();
        this.strongOpsInTentative = new HashMap<>();

        this.cab = cab;
        this.allOpsDoneListener = allOpsDoneListener;
        this.channelSupervisor = channelSupervisor;
        this.state = new StateObjectAdapter(postgresServer);
    }

    @Override
    public void executeOperation(pl.poznan.put.kacperwleklak.appcommon.db.request.Operation operation, ResponseGenerator client) {
        Operation creekOperation = AppCommonConverter.fromAppCommonOperation(operation);
        invoke(creekOperation, isReadOnly(creekOperation), isStrong(creekOperation), client);
    }

    private boolean isStrong(Operation operation) {
        return StringUtils.startsWithIgnoreCase(operation.getSql(), "update");
    }

    private boolean isReadOnly(Operation operation) {
        return StringUtils.startsWithIgnoreCase(operation.getSql(), "select");
    }

    // upon invoke(op : ops(F), strongOp : boolean), Alg I, l. 15
    public void invoke(Operation operation, boolean isReadOnly, boolean isStrong, ResponseGenerator client) {
        synchronized (lock) {
            if (isReadOnly) {
                handleReadOnlyOperation(operation, client);
                return;
            }
            currentEventNumber++;
            Dot eventID = new Dot(Integer.valueOf(replicaId).byteValue(), currentEventNumber);
            Request request = new Request(getCurrentTime(), eventID, operation, isStrong);
            if (isStrong) {
                DottedVersionVector reqCausalCtx = new DottedVersionVector(causalCtx);
                for (ListIterator<Request> iterator = tentative.listIterator(tentative.size()); iterator.hasPrevious();) {
                    Request x = iterator.previous();
                    if (x.compareTo(request) < 0)
                        break;
                    reqCausalCtx.remove(x.getRequestID());
                }
                request.setCausalCtx(reqCausalCtx);
            }
            reqsAwaitingResp.put(request, new ResponseHandler(client));
            causalCtx.add(eventID);
            insertIntoTentative(request);
            broadcast(request);
            if (isStrong) {
                cab.cabCast(request.getRequestID().toCabMessageId(), 1);
            }
        }
    }

    private void handleReadOnlyOperation(Operation operation, ResponseGenerator client) {
        Response response = state.executeReadOnly(operation);
        ResponseHandler responseHandler = new ResponseHandler(client);
        responseHandler.setResponse(response);
        responseToClient(responseHandler);
    }

    @Override
    public boolean testSync(CabMessageID messageID) {
        return checkDep(Dot.fromCabMessageId(messageID));
    }

    @Override
    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
        Dot eventID = Dot.fromCabMessageId(messageID);
        boolean test = checkDep(eventID);
        if (!test) {
            log.debug("Async tested false, saving callback");
            callbackMap.putIfAbsent(eventID, predicateCallback);
            return false;
        }
        log.debug("Async tested true {}", eventID);
        return true;
    }

    // upon RB-deliver(r : Req)
    public void operationRequestHandler(Request request) {
        log.debug("Creek: OperationRequestHandler {}", request.getRequestID());
        if (request.getRequestID().getReplica() == (byte) replicaId) {
            log.debug("Creek: Operation request from myself, skipping");
            checkWaitingPredicates();
            return;
        }
        if (!request.isStrong() || causalCtx.isSuperSetOf(request.getCausalCtx())) {
            causalCtx.add(request.getRequestID());
            List<Request> readyToScheduleOps = new ArrayList<>(List.of(request));
            readyToScheduleOps = establishReadyToScheduleOps(readyToScheduleOps);
            insertIntoTentative(readyToScheduleOps);
        } else {
            missingContextOps.add(request);
        }
        checkWaitingPredicates();
    }

    private void checkWaitingPredicates() {
        Iterator<Map.Entry<Dot, CabPredicateCallback>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Dot, CabPredicateCallback> entry = iterator.next();
            Dot eventID = entry.getKey();
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
            if (causalCtx.isSuperSetOf(request.getCausalCtx())) {
                causalCtx.add(request.getRequestID());
                readyToScheduleOps.add(request);
                missingContextOpsIterator.remove();
                anythingUpdated = true;
            }
        }
        return anythingUpdated
                ? establishReadyToScheduleOps(readyToScheduleOps)
                : readyToScheduleOps;
    }

    private void commit(Request request) {
        int newCommits = 0;
        int firstNotCommitted = 0;
        for (int i = 0; i < tentative.size(); i++) {
            Request x = tentative.get(i);
            if (request.getCausalCtx().contains(x.getRequestID()) || x == request) {
                if (firstNotCommitted == i)
                    firstNotCommitted++;
                newCommits++;
                if (x != request)
                    committed.add(x);
                if (x.isStrong())
                    strongOpsInTentative.remove(x.getRequestID().toCabMessageId());
            }
            else {
                if (newCommits > 0)
                    tentative.set(i - newCommits, x);
            }
        }
        for (int i = 0; i < newCommits; i++)
            tentative.remove(tentative.size() - 1);
        committed.add(request);
        if (firstNotCommitted < newCommits)
            adjustExecution(firstNotCommitted - newCommits); // negative value means that the firstChangedInTentative is already on the committed list
        for (int i = committed.size() - newCommits; i < committed.size(); i++) {
            Request strongOpsToCheckRequest = committed.get(i);
            ResponseHandler responseHandler = reqsAwaitingResp.get(strongOpsToCheckRequest);
            if (responseHandler != null && responseHandler.hasResponse() && executed.size() > i) {
                responseToClient(responseHandler);
                reqsAwaitingResp.remove(strongOpsToCheckRequest);
            }
        }
    }

    // procedure commit(r : Req)
//    private void commit(Request request) {
//        strongOpsInTentative.remove(request.getRequestID().toCabMessageId());
//        List<Request> committedExt = tentative.stream()
//                .filter(tentativeRequest -> request.getCausalCtx().contains(tentativeRequest.getRequestID()))
//                .peek(tentativeRequest -> {strongOpsInTentative.remove(tentativeRequest.getRequestID().toCabMessageId());})
//                .collect(Collectors.toList());
//        ArrayList<Request> newTentative = tentative.stream()
//                .filter(tentativeRequest -> !tentativeRequest.equals(request))
//                .filter(tentativeRequest -> !committedExt.contains(tentativeRequest))
//                .collect(Collectors.toCollection(ArrayList::new));
//        committed.addAll(committedExt);
//        committed.add(request);
//        tentative = newTentative;
//        List<Request> newOrder = CollectionUtils.concatLists(committed, tentative);
//        adjustExecution(newOrder);
//        List<Request> strongOpsToCheck = committedExt.stream()
//                .filter(Request::isStrong)
//                .collect(Collectors.toList());
//        strongOpsToCheck.add(request);
//        strongOpsToCheck.forEach(strongOpsToCheckRequest -> {
//            ResponseHandler responseHandler = reqsAwaitingResp.get(strongOpsToCheckRequest);
//            if (responseHandler != null && responseHandler.hasResponse() && executed.contains(strongOpsToCheckRequest)) {
//                responseToClient(responseHandler);
//                reqsAwaitingResp.remove(strongOpsToCheckRequest);
//            }
//        });
//    }

    //upon CAB-deliver(id : pair〈int, int〉)
    @Override
    public void cabDelver(CabMessageID cabMessageID) {
        synchronized (lock) {
            log.debug("CAB Delivered: {}", cabMessageID);
            Request request = strongOpsInTentative.get(cabMessageID);
            if (request != null)
                commit(request);
        }
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    private void broadcast(Request request) {
        channelSupervisor.rCast(request);
    }

    private void insertIntoTentative(Request request) {
        insertIntoTentative(Collections.singletonList(request));
    }

    private void insertIntoTentative(List<Request> readyToScheduleOps) {
        int minInsPoint = tentative.size();
        for (Request r : readyToScheduleOps) {
            allRequests.put(r.getRequestID(), r);
            if (r.isStrong()) {
                strongOpsInTentative.put(r.getRequestID().toCabMessageId(), r);
            }
            // res = (-(insertion point) - 1)
            // res + 1 = -(insertion point)
            // insertion point = -(res + 1)
            int res = Collections.binarySearch(tentative, r, Request::compareTo);
            assert res < 0;
            int insertionPoint = -(res + 1);
            if (insertionPoint < minInsPoint)
                minInsPoint = insertionPoint;
            tentative.add(insertionPoint, r);
        }
        adjustExecution(minInsPoint);
    }

    private void adjustExecution(int firstChangedInTentative) {
        synchronized (lock) {
            int commonLength = Math.min(executed.size(), committed.size() + firstChangedInTentative);

            for (int i = executed.size() - 1; i >= commonLength; i--) {
                toBeRolledBack.add(executed.remove(i));
            }
        }
    }

    private boolean checkDep(Dot eventID) {
        Request request = allRequests.get(eventID);
        if (request == null)
            return false;
        return causalCtx.isSuperSetOf(request.getCausalCtx());
    }

    private void responseToClient(Request request, Response response) {
        ResponseHandler responseHandler = reqsAwaitingResp.get(request);
        responseHandler.setResponse(response);
        responseToClient(responseHandler);
    }

    private void responseToClient(ResponseHandler responseHandler) {
        responseHandler.sendResponse();
    }

    private ResponseHandler updateReqsAwaitingResponse(Request request, Response response) {
        ResponseHandler responseHandler = reqsAwaitingResp.get(request);
        responseHandler.setResponse(response);
        reqsAwaitingResp.put(request, responseHandler);
        return responseHandler;
    }


    public void executeSingleStep() {
        log.debug("ESS tbRB={} tbE={}", toBeRolledBack);
        if (!toBeRolledBack.isEmpty()) {
            Request request = toBeRolledBack.get(0);
            state.rollback(request);
            toBeRolledBack.remove(0);
        } else if (executed.size() < committed.size() + tentative.size()) {
            Request request = executed.size() < committed.size() ? committed.get(executed.size()) : tentative.get(executed.size() - committed.size());
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
        } else {
            allOpsDoneListener.notifyNothingToDo();
        }
    }

    public void rDeliver(Request request) {
        operationRequestHandler(request);
    }
}
