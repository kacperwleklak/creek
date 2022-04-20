package pl.poznan.put.kacperwleklak.cab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.exception.CabCastException;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;
import pl.poznan.put.kacperwleklak.communication.MessageReceiver;
import pl.poznan.put.kacperwleklak.communication.ReplicasMessagingService;
import pl.poznan.put.kacperwleklak.creek.Creek;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.impl.CabAcceptMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabBroadcastMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabProposeMessage;
import pl.poznan.put.kacperwleklak.structure.IncrementalIndexList;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CAB {

    private int sequenceNumber;
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
        replicasMessagingService.sendMessage(MessageReceiver.LEADER, cabBroadcastMessage.toCreekMsg());
    }

    public void broadcastEventHandler(CreekMsg creekMsg) throws CabCastException {
        if (!isLeader) {
            throw new CabCastException("Unable to broadcast message. Not a leader!");
        }
        CabBroadcastMessage cabBroadcastMessage = CabBroadcastMessage.from(creekMsg);
        CabMessage cabMessage = cabBroadcastMessage.getCabMessage();
        int index = received.add(cabMessage);
        CabProposeMessage cabProposeMessage = new CabProposeMessage(cabMessage, index, sequenceNumber);
        replicasMessagingService.broadcastMessage(cabProposeMessage.toCreekMsg());
    }

    public void proposeEventHandler(CreekMsg creekMsg) throws CabCastException {
        CabProposeMessage cabProposeMessage = CabProposeMessage.from(creekMsg);
        if (cabProposeMessage.getSequenceNumber() != sequenceNumber) {
            throw new CabCastException("Received proposition with invalid sequence number");
        }
        if (received.get(cabProposeMessage.getIndex()) != null) {
            throw new CabCastException("Received proposition for value that currently exist!");
        }
        received.put(cabProposeMessage.getIndex(), cabProposeMessage.getCabMessage());
        CabAcceptMessage cabAcceptMessage = new CabAcceptMessage(cabProposeMessage.getCabMessage().getMessageId(),
                cabProposeMessage.getSequenceNumber());
        replicasMessagingService.broadcastMessage(cabAcceptMessage.toCreekMsg());
    }


    public void acceptEventHandler(CreekMsg creekMsg) throws CabCastException {
        CabAcceptMessage cabAcceptMessage = CabAcceptMessage.from(creekMsg);
        if (cabAcceptMessage.getSequenceNumber() != sequenceNumber) {
            throw new CabCastException("Received proposition with invalid sequence number");
        }
        String messageID = cabAcceptMessage.getUUID();
        acceptsReceived.putIfAbsent(messageID, 0);
        Integer currentAcceptsReceived = acceptsReceived.computeIfPresent(messageID, (key, value) -> value + 1);
        if (isMajority(currentAcceptsReceived)) {
            deliverMessage(messageID);
        }
    }

    private void deliverMessage(String messageID) {
        int index = received.indexOf(new CabMessage(messageID, null));
        if (index == nextIndexToDeliver) {
            CabMessage cabMessage = received.get(index);
            if (cabMessage.getPredicate().isTrue(null)) {
                cabDeliver(messageID);
                nextIndexToDeliver++;
                if (waitingToDeliver.get(nextIndexToDeliver) != null) {
                    String removedMessageIndex = waitingToDeliver.remove(nextIndexToDeliver);
                    deliverMessage(removedMessageIndex);
                }
            } else {
                waitingToDeliver.put(index, messageID);
            }
        }
        else if (index > nextIndexToDeliver) {
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
