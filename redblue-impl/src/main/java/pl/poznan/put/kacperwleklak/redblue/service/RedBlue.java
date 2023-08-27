package pl.poznan.put.kacperwleklak.redblue.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.springframework.context.annotation.DependsOn;
import pl.poznan.put.kacperwleklak.appcommon.db.OperationExecutor;
import pl.poznan.put.kacperwleklak.appcommon.db.PostgresServer;
import pl.poznan.put.kacperwleklak.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.appcommon.db.response.Response;
import pl.poznan.put.kacperwleklak.appcommon.db.response.ResponseHandler;
import pl.poznan.put.kacperwleklak.common.utils.CollectionUtils;
import pl.poznan.put.kacperwleklak.redblue.interfaces.RedBlueNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.model.OwnRequest;
import pl.poznan.put.kacperwleklak.redblue.protocol.EventID;
import pl.poznan.put.kacperwleklak.redblue.protocol.Operation;
import pl.poznan.put.kacperwleklak.redblue.protocol.PassToken;
import pl.poznan.put.kacperwleklak.redblue.protocol.Request;
import pl.poznan.put.kacperwleklak.redblue.state.GeneratorOpResult;
import pl.poznan.put.kacperwleklak.redblue.utils.AppCommonConverter;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ConcurrentZMQChannelSupervisor;

import java.util.*;

@Slf4j
@DependsOn({"messageUtils"})
public class RedBlue implements OperationExecutor {

    private final byte REPLICA_ID;
    private final RedBlueNotificationReceiver redBlueNotificationReceiver;
    private final ConcurrentZMQChannelSupervisor concurrentZMQChannelSupervisor;
    private final List<String> replicas;

    private long currEventNumber = 0;
    private long currentRedNumber = 0;
    private long tokenRedNumber = -1;
    private final Set<EventID> causalContext = new HashSet<>();
    private final Queue<OwnRequest> pendingOwnRedOps = new ArrayDeque<>();
    private final RedBlueStateObjectAdapter stateObject;
    private final Map<EventID, List<Request>> pendingRequests = new HashMap<>();

    public RedBlue(ConcurrentZMQChannelSupervisor concurrentZMQChannelSupervisor, int replicaId, PostgresServer postgresServer, List<String> replicas,
                   RedBlueNotificationReceiver redBlueNotificationReceiver) {
        REPLICA_ID = (byte) replicaId;
        if (REPLICA_ID == 1) tokenRedNumber = 0;
        this.replicas = replicas;
        this.concurrentZMQChannelSupervisor = concurrentZMQChannelSupervisor;
        this.redBlueNotificationReceiver = redBlueNotificationReceiver;
        this.stateObject = new RedBlueStateObjectAdapter(postgresServer);
    }

    //upon RB-deliver(r : Req)
    public void operationRequestHandler(Request request) {
        if (REPLICA_ID == request.getRequestID().getReplica()) {
            //issued locally
            return;
        }
        long reqRedNumber = request.getRedNumber();
        if (causalContext.containsAll(request.getCasualCtx())) {
            stateObject.executeShadow(request);
            if (reqRedNumber > 0) {
                currentRedNumber += 1;
            }
            causalContext.add(request.getRequestID());
            checkPendingRequests(request, 0);
        } else {
            addMissingDot(request);
        }
    }

    //procedure checkPendingRequests(newReq : Req)
    private void checkPendingRequests(Request newReq, int depth) {
        List<Request> requests = pendingRequests.get(newReq.getRequestID());
        for (Request pendingRequest : requests) {
            log.info("checkpendingrequests depth={}", depth);
            if (causalContext.containsAll(pendingRequest.getCasualCtx())) {
                stateObject.executeShadow(pendingRequest);
                if (pendingRequest.getRedNumber() >= 0) {
                    currentRedNumber += 1;
                }
                causalContext.add(pendingRequest.getRequestID());
                checkPendingRequests(pendingRequest, depth+1);
            } else {
                addMissingDot(pendingRequest);
            }
        }
    }

    private void addMissingDot(Request request) {
        EventID missingDot = maxEventId(request);
        log.info("missingDot = {}", missingDot);
        pendingRequests.putIfAbsent(missingDot, new ArrayList<>());
        pendingRequests.get(missingDot).add(request);
    }

    public void tokenTimeIsUp() {
        byte nextReplica = (byte) ((REPLICA_ID % replicas.size()) + 1);
        log.debug("Sending token={} to replica {}", currentRedNumber, (int) nextReplica);
        PassToken passTokenMsg = new PassToken(currentRedNumber, nextReplica);
        rbCast(passTokenMsg);
        tokenRedNumber = -1;
    }

    public void passTokenHandler(PassToken passToken) {
        if (REPLICA_ID == passToken.getRecipient()) {
            tokenRedNumber = passToken.getRedNumber();
            if (isTokenRedNumberAndPendingOwnRequests()) {
                redBlueNotificationReceiver.hasTokenAndPendingOwnRedOps();
            }
        }
    }

    //upon tokenRedNumber != ⊥ and !pendingOwnRedOps.empty()
    public void whenIsTokenRedNumberAndPendingOwnRequests() {
        log.debug("whenIsTokenRedNumberAndPendingOwnRequests called");
        if (isTokenRedNumberAndPendingOwnRequests()) {
            OwnRequest polledRequest = pendingOwnRedOps.poll();
            log.debug("ownRequest polled, remaining size={}", pendingOwnRedOps.size());
            executeOperation(polledRequest.getOperation(), polledRequest.getClient());
            if (!pendingOwnRedOps.isEmpty()) {
                log.debug("getting another stored own request");
                redBlueNotificationReceiver.hasTokenAndPendingOwnRedOps();
            }
        }
    }

    private boolean isStrong(Operation operation) {
        return StringUtils.startsWithIgnoreCase(operation.getSql(), "call");
    }

    @Override
    //upon invoke(op : ops(F), strongOp : boolean)
    public void executeOperation(pl.poznan.put.kacperwleklak.appcommon.db.request.Operation dbOperation, ResponseGenerator client) {
        Operation operation = AppCommonConverter.fromAppCommonOperation(dbOperation);
        executeOperation(operation, client);
    }

    private void executeOperation(Operation operation, ResponseGenerator client) {
        log.debug("Invoking operation {}", operation);
        boolean strongOp = isStrong(operation);
        if (strongOp && (tokenRedNumber < 0 || currentRedNumber < tokenRedNumber)) {
            log.debug("Unable to perform strongOp tokenRedNumber={}, currentRedNumber={}, {}", tokenRedNumber, currentRedNumber, operation);
            pendingOwnRedOps.add(new OwnRequest(client, operation));
            return;
        }
        GeneratorOpResult generatorOpResult = stateObject.executeGenerator(operation);
        Response response = generatorOpResult.getResponse();
        Operation shadowOperation = generatorOpResult.getShadowOp();
        log.debug("Generated shadow op {}", shadowOperation);
        if (shadowOperation != null && shadowOperation.isSetSql()) {
            long redNumber = -1;
            if (strongOp) {
                redNumber = currentRedNumber += 1;
            }
            currEventNumber += 1;
            EventID eventID = new EventID(REPLICA_ID, currEventNumber);
            Request request = new Request(eventID, redNumber, shadowOperation, strongOp, new HashSet<>(causalContext));
            rbCast(request);
            Response shadowResponse = stateObject.executeShadow(request);
            if (Objects.isNull(response)) response = shadowResponse;
            causalContext.add(eventID);
        }
        ResponseHandler responseHandler = new ResponseHandler(client);
        responseHandler.setResponse(response);
        responseHandler.sendResponse();
    }

    private void rbCast(TBase message) {
        concurrentZMQChannelSupervisor.rCast(message);
    }

    private boolean isTokenRedNumberAndPendingOwnRequests() {
        boolean condition = 0 <= tokenRedNumber && !pendingOwnRedOps.isEmpty();
        log.debug("isTokenRedNumberAndPendingOwnRequests = {}", condition);
        return condition;
    }

    private EventID maxEventId(Request request) {
        Set<EventID> eventIds = CollectionUtils.differenceToSet(request.getCasualCtx(), causalContext);
        return eventIds.stream()
                .max(Comparator
                        .comparing(EventID::getReplica)
                        .thenComparing(EventID::getCurrEventNo))
                .orElseThrow(() -> new RuntimeException("Unable to find max EventID"));
    }
}
