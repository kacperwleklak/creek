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
import pl.poznan.put.kacperwleklak.redblue.protocol.*;
import pl.poznan.put.kacperwleklak.redblue.state.GeneratorOpResult;
import pl.poznan.put.kacperwleklak.redblue.utils.AppCommonConverter;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ConcurrentZMQChannelSupervisor;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final DottedVersionVector causalContext;
    private final Queue<OwnRequest> pendingOwnRedOps = new ArrayDeque<>();
    private final RedBlueStateObjectAdapter stateObject;
    private final Map<Dot, List<Request>> pendingRequests = new HashMap<>();

    public RedBlue(ConcurrentZMQChannelSupervisor concurrentZMQChannelSupervisor, int replicaId, PostgresServer postgresServer, List<String> replicas,
                   RedBlueNotificationReceiver redBlueNotificationReceiver) {
        REPLICA_ID = (byte) replicaId;
        if (REPLICA_ID == 1) tokenRedNumber = 0;
        this.replicas = replicas;
        this.causalContext = new DottedVersionVector(replicas.size());
        this.concurrentZMQChannelSupervisor = concurrentZMQChannelSupervisor;
        this.redBlueNotificationReceiver = redBlueNotificationReceiver;
        this.stateObject = new RedBlueStateObjectAdapter(postgresServer);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::printDiagnostics, 20, 10, TimeUnit.SECONDS);
    }

    //upon RB-deliver(r : Req)
    public void operationRequestHandler(Request request) {
        if (REPLICA_ID == request.getRequestID().getReplica()) {
            //issued locally
            return;
        }
        long reqRedNumber = request.getRedNumber();
        if (causalContext.isSuperSetOf(request.getCausalCtx())) {
            stateObject.executeShadow(request);
            if (reqRedNumber > 0) {
                currentRedNumber += 1;
            }
            causalContext.add(request.getRequestID());
            checkPendingRequests(request);
        } else {
            addMissingDot(request);
        }
    }

    private void printDiagnostics() {
        log.info("pendingOwnRedOps={}", pendingOwnRedOps.size());
    }

    //procedure checkPendingRequests(newReq : Req)
    private void checkPendingRequests(Request newReq) {
        List<Request> requests = pendingRequests.remove(newReq.getRequestID());
        if (requests == null) {
            return;
        }
        for (Request pendingRequest : requests) {
            if (causalContext.isSuperSetOf(pendingRequest.getCausalCtx())) {
                stateObject.executeShadow(pendingRequest);
                if (pendingRequest.getRedNumber() >= 0) {
                    currentRedNumber += 1;
                }
                causalContext.add(pendingRequest.getRequestID());
                checkPendingRequests(pendingRequest);
            } else {
                addMissingDot(pendingRequest);
            }
        }
    }

    private void addMissingDot(Request request) {
        Dot missingDot = maxEventId(request);
        pendingRequests.putIfAbsent(missingDot, new ArrayList<>());
        pendingRequests.get(missingDot).add(request);
    }

    public void tokenTimeIsUp() {
        byte nextReplica = (byte) ((REPLICA_ID+1) % replicas.size());
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
            Dot eventID = new Dot(REPLICA_ID, currEventNumber);
            Request request = new Request(eventID, redNumber, shadowOperation, strongOp, new DottedVersionVector(causalContext));
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

    private Dot maxEventId(Request request) {
        return request.getCausalCtx().maxDot(causalContext);
    }
}
