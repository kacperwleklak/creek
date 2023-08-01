package pl.poznan.put.kacperwleklak.redblue.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import pl.poznan.put.appcommon.db.OperationExecutor;
import pl.poznan.put.appcommon.db.PostgresServer;
import pl.poznan.put.appcommon.db.ResponseGenerator;
import pl.poznan.put.appcommon.db.response.Response;
import pl.poznan.put.appcommon.db.response.ResponseHandler;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.redblue.interfaces.RedBlueNotificationReceiver;
import pl.poznan.put.kacperwleklak.redblue.model.OwnRequest;
import pl.poznan.put.kacperwleklak.redblue.protocol.EventID;
import pl.poznan.put.kacperwleklak.redblue.protocol.Operation;
import pl.poznan.put.kacperwleklak.redblue.protocol.PassToken;
import pl.poznan.put.kacperwleklak.redblue.protocol.Request;
import pl.poznan.put.kacperwleklak.redblue.state.GeneratorOpResult;
import pl.poznan.put.kacperwleklak.redblue.utils.AppCommonConverter;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;

import java.util.*;

@Slf4j
@DependsOn({"messageUtils"})
public class RedBlue implements OperationExecutor {

    private final byte REPLICA_ID;
    private final RedBlueNotificationReceiver redBlueNotificationReceiver;
    private final ReliableChannel reliableChannel;
    private final List<String> replicas;

    private long currEventNumber = 0;
    private long currentRedNumber = 0;
    private long tokenRedNumber = 0;
    private Set<EventID> casualContext = new HashSet<>();
    private Queue<OwnRequest> pendingOwnRedOps = new ArrayDeque<>();
    private final RedBlueStateObjectAdapter stateObject;
    private final List<Request> pendingRequests = new ArrayList<>();

    @Autowired
    public RedBlue(ReliableChannel reliableChannel, int replicaId, PostgresServer postgresServer, List<String> replicas,
                   RedBlueNotificationReceiver redBlueNotificationReceiver) {
        //TODO constructor params
        REPLICA_ID = (byte) replicaId;
        this.replicas = replicas;
        this.reliableChannel = reliableChannel;
        this.redBlueNotificationReceiver = redBlueNotificationReceiver;
        this.stateObject = new RedBlueStateObjectAdapter(postgresServer);
    }

    public void operationRequestHandler(Request request) {
        if (REPLICA_ID == request.getRequestID().getReplica()) {
            //issued locally
            return;
        }
        long reqRedNumber = request.getRedNumber();
        if (isInCasualCtxAndRedNumberOk(request)) {
            stateObject.executeShadow(request);
            if (reqRedNumber != 0) {
                currentRedNumber += 1;
            }
            casualContext.add(request.getRequestID());
            redBlueNotificationReceiver.pendingRequestsFlagIsTrue();
        } else {
            pendingRequests.add(request);
        }
    }

    private boolean isInCasualCtxAndRedNumberOk(Request request) {
        long reqRedNumber = request.getRedNumber();
        return casualContext.containsAll(request.getCasualCtx()) &&
                (reqRedNumber == 0 || reqRedNumber == currentRedNumber);
    }

    // upon pendingRequestsFlag = true
    public void whenIsPendingRequestFlag() {
        for (Request pendingRequest : pendingRequests) {
            if (isInCasualCtxAndRedNumberOk(pendingRequest)) {
                stateObject.executeShadow(pendingRequest);
                if (pendingRequest.getRedNumber() != 0) {
                    currentRedNumber += 1;
                }
                casualContext.add(pendingRequest.getRequestID());
                redBlueNotificationReceiver.pendingRequestsFlagIsTrue();
            }
        }
    }

    public void tokenTimeIsUp() {
        byte nextReplica = (byte) ((REPLICA_ID % replicas.size()) + 1);
        log.debug("Sending token to replica {}", (int) nextReplica);
        PassToken passTokenMsg = new PassToken(tokenRedNumber + 1, nextReplica);
        rbCast(passTokenMsg);
        tokenRedNumber = 0;
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
    public void executeOperation(pl.poznan.put.appcommon.db.request.Operation dbOperation, ResponseGenerator client) {
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
            long redNumber = 0;
            if (strongOp) {
                redNumber = currentRedNumber += 1;
            }
            currEventNumber += 1;
            EventID eventID = new EventID(REPLICA_ID, currEventNumber);
            Request request = new Request(eventID, redNumber, shadowOperation, strongOp, casualContext);
            rbCast(request);
            response = stateObject.executeShadow(request);
            casualContext.add(eventID);
        }
        ResponseHandler responseHandler = new ResponseHandler(client);
        responseHandler.setResponse(response);
        responseHandler.sendResponse();
    }

    private void rbCast(TBase message) {
        log.debug("Broadcasting event {}", message);
        try {
            reliableChannel.rCast(ThriftSerializer.serialize(message));
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private boolean isTokenRedNumberAndPendingOwnRequests() {
        boolean condition = 0 <= tokenRedNumber && !pendingOwnRedOps.isEmpty();
        log.debug("isTokenRedNumberAndPendingOwnRequests = {}", condition);
        return condition;
    }
}
