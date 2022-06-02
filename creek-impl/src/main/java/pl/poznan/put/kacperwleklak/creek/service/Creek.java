package pl.poznan.put.kacperwleklak.creek.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import pl.poznan.put.kacperwleklak.cab.*;
import pl.poznan.put.kacperwleklak.common.utils.CollectionUtils;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;
import pl.poznan.put.kacperwleklak.creek.message.CreekMsg;
import pl.poznan.put.kacperwleklak.creek.message.impl.OperationRequestMessage;
import pl.poznan.put.kacperwleklak.creek.structure.Request;
import pl.poznan.put.kacperwleklak.creek.structure.Response;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@DependsOn({"messageUtils"})
public class Creek implements ReliableChannelDeliverListener, CabDeliverListener, CabPredicate {

    private static final int PREDICATE_ID = 1;

    // current state variables
    private int currentEventNumber;
    private String replicaId;
    private final Set<Request.EventID> casualCtx;
    private List<Request> tentative;
    private List<Request> committed;
    private List<Request> executed;
    private List<Request> toBeExecuted;
    private List<Request> toBeRolledBack;
    private final Map<Request, Response> reqsAwaitingResp;
    private final Set<Request> missingContextOps;
    private final StateObject state;
    private final HashMap<Request.EventID, CabPredicateCallback> callbackMap;

    // dependencies
    private final CAB cab;
    private final ReliableChannel reliableChannel;

    // temporary
    private final Map<Request, Response> responsesMap;


    @Autowired
    public Creek(CAB cab, ReliableChannel reliableChannel) {
        this.currentEventNumber = 0;
        this.casualCtx = new HashSet<>();
        this.tentative = new ArrayList<>();
        this.committed = new ArrayList<>();
        this.executed = new ArrayList<>();
        this.toBeExecuted = new ArrayList<>();
        this.toBeRolledBack = new ArrayList<>();
        this.reqsAwaitingResp = new HashMap<>();
        this.missingContextOps = new HashSet<>();
        this.state = new StateObject();
        this.callbackMap = new HashMap<>();

        this.cab = cab;
        this.reliableChannel = reliableChannel;

        this.responsesMap = new HashMap<>();
    }

    @PostConstruct
    public void postInitialization() {
        reliableChannel.registerListener(this);
        this.replicaId = MessageUtils.myAddress();
        cab.registerListener(this);
        cab.start(Map.of(PREDICATE_ID, this));
    }

    // upon invoke(op : ops(F), strongOp : boolean), Alg I, l. 15
    public void invoke(String operation, boolean isStrong) {
        currentEventNumber++;
        Request.EventID eventID = new Request.EventID(replicaId, currentEventNumber);
        Request request = new Request(getCurrentTime(), eventID, operation, isStrong);
        if (isStrong) {
            List<Request.EventID> tentativeGreaterThanCurrentRequest = tentative
                    .stream()
                    .filter(request1 -> request1.compareTo(request) > 0)
                    .map(Request::getRequestID)
                    .collect(Collectors.toList());
            request.setCasualCtx(CollectionUtils.differenceToSet(casualCtx, tentativeGreaterThanCurrentRequest));
            cab.cabCast(request.getRequestID(), 1);
        }
        casualCtx.add(eventID);
        broadcast(new OperationRequestMessage(request));
        insertIntoTentative(request);
        reqsAwaitingResp.put(request, null);
    }

    @Override
    public boolean testSync(CabMessageID messageID) {
        return checkDep((Request.EventID) messageID);
    }

    @Override
    public boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback) {
        Request.EventID eventID = (Request.EventID) messageID;
        boolean test = checkDep(eventID);
        if (!test) {
            callbackMap.put(eventID, predicateCallback);
            return false;
        }
        return true;
    }

    // reliable channel deliver callback
    @Override
    public void rDeliver(byte[] msg) {
        Object deserialized = SerializationUtils.deserialize(msg);
        if (deserialized instanceof OperationRequestMessage) {
            operationRequestHandler(((OperationRequestMessage) deserialized).getRequest());
        }
    }

    // upon RB-deliver(r : Req)
    private void operationRequestHandler(Request request) {
        if (request.getRequestID().getReplicaId().equals(replicaId)) {
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

    private void checkWaitingPredicates() {
        Iterator<Map.Entry<Request.EventID, CabPredicateCallback>> iterator = callbackMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Request.EventID, CabPredicateCallback> entry = iterator.next();
            Request.EventID eventID = entry.getKey();
            boolean test = checkDep(eventID);
            if (test) {
                entry.getValue().predicateBecomesTrue(PREDICATE_ID, eventID);
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
        committed = CollectionUtils.concatLists(committedExt, committedExt);
        committed.add(request);
        tentative = newTentative;
        List<Request> newOrder = CollectionUtils.concatLists(committed, tentative);
        adjustExecution(newOrder);
        List<Request> strongOpsToCheck = committedExt.stream()
                .filter(Request::isStrong)
                .collect(Collectors.toList());
        strongOpsToCheck.add(request);
        strongOpsToCheck.forEach(strongOpsToCheckRequest -> {
            Response response = reqsAwaitingResp.get(strongOpsToCheckRequest);
            if (response != null && executed.contains(strongOpsToCheckRequest)) {
                response.send();
                reqsAwaitingResp.remove(strongOpsToCheckRequest);
            }
        });
    }

    //upon CAB-deliver(id : pair〈int, int〉)
    @Override
    public void cabDelver(CabMessageID cabMessageID) {
        log.debug("CAB Delivered: {}", cabMessageID);
        tentative.stream()
                .filter(request -> request.getRequestID().equals(cabMessageID))
                .findAny()
                .ifPresent(this::commit);
    }

    private long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    private void broadcast(CreekMsg creekMsg) {
        reliableChannel.rCast(SerializationUtils.serialize(creekMsg));
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
        List<Request> inOrder = CollectionUtils.longestCommonPrefix(executed, newOrder);
        List<Request> outOfOrder = CollectionUtils.differenceToList(executed, inOrder);
        executed = inOrder;
        toBeExecuted = CollectionUtils.differenceToList(newOrder, executed);
        Collections.reverse(outOfOrder);
        toBeRolledBack = CollectionUtils.concatLists(toBeRolledBack, outOfOrder);
        performExecutions();
    }

    private boolean checkDep(Request.EventID eventID) {
        return Stream.concat(committed.stream(), tentative.stream())
                .filter(request -> eventID.equals(request.getRequestID()))
                .findAny()
                .map(request -> casualCtx.containsAll(request.getCasualCtx()))
                .orElse(false);
    }

    private void responseToClient(Request request, Response response) {
        // temporary solution - publishing list of responses
        responsesMap.put(request, response);
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
                        reqsAwaitingResp.put(request, response);
                        responseToClient(request, response);
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

    /*
     *
     * Temporary methods only for test purposes
     *
     */

    public StateObject getStateObject() {
        return state;
    }

    public Map<Request, Response> getResponsesMap() {
        return responsesMap;
    }
}
