package pl.poznan.put.kacperwleklak.cab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.exception.CabCastException;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;
import pl.poznan.put.kacperwleklak.communication.MessageReceiver;
import pl.poznan.put.kacperwleklak.communication.ReplicasMessagingService;
import pl.poznan.put.kacperwleklak.creek.Creek;
import pl.poznan.put.kacperwleklak.creek.OperationRequest;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.impl.AckMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabAcceptMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabBroadcastMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabProposeMessage;
import pl.poznan.put.kacperwleklak.operation.CreekOperation;
import pl.poznan.put.kacperwleklak.structure.IncrementalIndexList;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CAB {

    private final int sequenceNumber;
    private int nextIndexToDeliver;
    private final IncrementalIndexList<CabMessage> received;
    private final IncrementalIndexList<String> waitingToDeliver;
    private final ConcurrentHashMap<String, Integer> acceptsReceived;
    private boolean isLeader = false;

    private final ReplicasMessagingService replicasMessagingService;
    private final Creek creek;


    private int replicasNumber;

    @Autowired
    public CAB(@Lazy ReplicasMessagingService replicasMessagingService, @Lazy Creek creek) {
        this.sequenceNumber = 0;
        this.nextIndexToDeliver = 0;
        this.received = new IncrementalIndexList<>();
        this.waitingToDeliver = new IncrementalIndexList<>();
        this.acceptsReceived = new ConcurrentHashMap<>();
        this.replicasMessagingService = replicasMessagingService;
        this.creek = creek;
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public void setReplicasNumber(int replicasNumber) {
        this.replicasNumber = replicasNumber;
    }

    public void cabCast(String UUID, CabPredicate cabPredicate) {
        CabBroadcastMessage cabBroadcastMessage = new CabBroadcastMessage();
        CabMessage cabMessage = new CabMessage(UUID, cabPredicate);
        cabBroadcastMessage.setCabMessage(cabMessage);
        replicasMessagingService.sendMessage(MessageReceiver.LEADER, cabBroadcastMessage);
    }

    //upon BroadcastMessage(UUIDm, q)
    public CreekMsg broadcastEventHandler(CabBroadcastMessage cabBroadcastMessage) throws CabCastException {
        if (!isLeader) {
            throw new CabCastException("Unable to broadcast message. Not a leader!");
        }
        CabMessage cabMessage = cabBroadcastMessage.getCabMessage();
        int index = received.add(cabMessage);
        CabProposeMessage cabProposeMessage = new CabProposeMessage(cabMessage, index, sequenceNumber);
        replicasMessagingService.broadcastMessage(cabProposeMessage);
        return new AckMessage();
    }

    //upon Propose(UUIDm, d, receivedSequenceNumber, q)
    public CreekMsg proposeEventHandler(CabProposeMessage cabProposeMessage) throws CabCastException {
        if (cabProposeMessage.getSequenceNumber() != sequenceNumber) {
            throw new CabCastException("Received proposition with invalid sequence number");
        }
        if (received.get(cabProposeMessage.getIndex()) != null) {
            throw new CabCastException("Received proposition for value that currently exist!");
        }
        received.put(cabProposeMessage.getIndex(), cabProposeMessage.getCabMessage());
        CabAcceptMessage cabAcceptMessage = new CabAcceptMessage(cabProposeMessage.getCabMessage().getMessageId(),
                cabProposeMessage.getSequenceNumber());
        replicasMessagingService.broadcastMessage(cabAcceptMessage);
        return new AckMessage();
    }

    //upon Accept(UUIDm, receivedSequenceNumber)
    public CreekMsg acceptEventHandler(CabAcceptMessage cabAcceptMessage) throws CabCastException {
        if (cabAcceptMessage.getSequenceNumber() != sequenceNumber) {
            throw new CabCastException("Received proposition with invalid sequence number");
        }
        String messageID = cabAcceptMessage.getUUID();
        acceptsReceived.putIfAbsent(messageID, 0);
        Integer currentAcceptsReceived = acceptsReceived.computeIfPresent(messageID, (key, value) -> value + 1);
        if (isMajority(currentAcceptsReceived)) {
            deliverMessage(messageID);
        }
        return new AckMessage();
    }

    //upon PredicateBecomesTrue - check if any predicate becomes true due to creek message deliver
    public synchronized void newMessageDelivered(IncrementalIndexList<OperationRequest> operations) {
        CabMessage nextToBeDelivered = received.get(nextIndexToDeliver);
        if (nextToBeDelivered == null) {
            return;
        }
        if (nextToBeDelivered.getPredicate().isTrue(operations)) {
            deliverMessage(nextToBeDelivered.getMessageId());
        }
    }

    private synchronized void deliverMessage(String messageID) {
        int index = received.indexOf(new CabMessage(messageID, null));
        if (index == nextIndexToDeliver) {
            CabMessage cabMessage = received.get(index);
            if (cabMessage.getPredicate().isTrue(creek.getReceivedMessages())) {
                cabDeliver(messageID);
                nextIndexToDeliver++;
                if (waitingToDeliver.get(nextIndexToDeliver) != null) {
                    String removedMessageIndex = waitingToDeliver.remove(nextIndexToDeliver);
                    deliverMessage(removedMessageIndex);
                }
            } else {
                waitingToDeliver.put(index, messageID);
            }
        } else if (index > nextIndexToDeliver) {
            waitingToDeliver.put(index, messageID);
        }
    }

    private void cabDeliver(String messageID) {
        creek.cabDeliver(messageID);
    }

    private boolean isMajority(int replicasAcceptedNumber) {
        return replicasAcceptedNumber >= (replicasNumber * 0.5);
    }
}
