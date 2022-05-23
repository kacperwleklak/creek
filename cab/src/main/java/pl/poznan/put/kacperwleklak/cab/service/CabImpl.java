package pl.poznan.put.kacperwleklak.cab.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.message.CabMsg;
import pl.poznan.put.kacperwleklak.cab.message.impl.CabAcceptMessage;
import pl.poznan.put.kacperwleklak.cab.message.impl.CabBroadcastMessage;
import pl.poznan.put.kacperwleklak.cab.message.impl.CabProposeMessage;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;
import pl.poznan.put.kacperwleklak.common.structures.IncrementalIndexList;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CabImpl implements CAB, ReliableChannelDeliverListener, CabPredicateCallback {

    // state holders
    private final int sequenceNumber;
    private int nextIndexToDeliver;
    private Map<Integer, CabPredicate> predicates;

    // message holders
    private final IncrementalIndexList<CabMessage> received;
    private final IncrementalIndexList<UUID> waitingToDeliver;
    private final ConcurrentHashMap<UUID, Integer> acceptsReceived;

    private boolean isLeader = false;
    private String leaderAddr;
    private int replicasNumber;

    // communication and listeners
    private final ReliableChannel reliableChannel;
    private final Set<CabDeliverListener> listeners;

    @Autowired
    public CabImpl(ReliableChannel reliableChannel,
                   @Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                   @Value("${communication.replicas.host}") String myHost,
                   @Value("${communication.replicas.port}") int myPort) {
        this.sequenceNumber = 0;
        this.nextIndexToDeliver = 0;
        this.received = new IncrementalIndexList<>();
        this.waitingToDeliver = new IncrementalIndexList<>();
        this.acceptsReceived = new ConcurrentHashMap<>();
        this.listeners = new HashSet<>();
        this.reliableChannel = reliableChannel;
        setupReplicasValues(replicasAddresses, myHost, myPort);
    }

    @PostConstruct
    public void postConstruct() {
        reliableChannel.registerListener(this);
    }

    public void setupReplicasValues(List<String> replicasAddresses, String host, int port) {
        leaderAddr = replicasAddresses.get(0);
        if (MessageUtils.toAddressString(host, port).equals(leaderAddr)) {
            isLeader = true;
        }
        replicasNumber = replicasAddresses.size();
    }

    @Override
    public void cabCast(UUID msgId, int predicateId) {
        CabMessage cabMessage = new CabMessage(msgId, predicateId);
        CabBroadcastMessage cabBroadcastMessage = new CabBroadcastMessage(cabMessage);
        reliableChannel.rbSend(leaderAddr, SerializationUtils.serialize(cabBroadcastMessage));
    }

    //upon BroadcastMessage(UUIDm, q)
    public void broadcastEventHandler(CabBroadcastMessage cabBroadcastMessage) {
        if (!isLeader) {
            log.error("Unable to broadcast message. Not a leader!");
            return;
        }
        CabMessage cabMessage = cabBroadcastMessage.getCabMessage();
        int index = received.add(cabMessage);
        CabProposeMessage cabProposeMessage = new CabProposeMessage(cabMessage, index, sequenceNumber);
        broadcast(cabProposeMessage);
    }

    //upon Propose(UUIDm, d, receivedSequenceNumber, q)
    public void proposeEventHandler(CabProposeMessage cabProposeMessage)  {
        if (cabProposeMessage.getSequenceNumber() != sequenceNumber) {
            log.error("Received proposition with invalid sequence number");
            return;
        }
        if (received.get(cabProposeMessage.getIndex()) != null && !isLeader) {
            log.error("Received proposition for value that currently exist!");
            return;
        }
        received.put(cabProposeMessage.getIndex(), cabProposeMessage.getCabMessage());
        CabAcceptMessage cabAcceptMessage = new CabAcceptMessage(cabProposeMessage.getCabMessage().getMessageId(),
                cabProposeMessage.getSequenceNumber());
        broadcast(cabAcceptMessage);
    }

    //upon Accept(UUIDm, receivedSequenceNumber)
    public void acceptEventHandler(CabAcceptMessage cabAcceptMessage) {
        if (cabAcceptMessage.getSequenceNumber() != sequenceNumber) {
           log.error("Received proposition with invalid sequence number");
            return;
        }
        UUID messageID = cabAcceptMessage.getUuid();
        acceptsReceived.putIfAbsent(messageID, 0);
        Integer currentAcceptsReceived = acceptsReceived.computeIfPresent(messageID, (key, value) -> value + 1);
        if (isMajority(currentAcceptsReceived)) {
            deliverMessage(messageID);
        }
    }

    private boolean isPredicateTrue(CabMessage cabMessage) {
        CabPredicate cabPredicate = predicates.get(cabMessage.getPredicateId());
        return cabPredicate.testAsync(cabMessage.getMessageId(), this);
    }

    private void broadcast(CabMsg cabMsg) {
        reliableChannel.rbCast(SerializationUtils.serialize(cabMsg));
    }

    private synchronized void deliverMessage(UUID messageID) {
        int index = received.indexOf(new CabMessage(messageID));
        if (index == nextIndexToDeliver) {
            CabMessage cabMessage = received.get(index);
            if (isPredicateTrue(cabMessage)) {
                cabDeliver(messageID);
                nextIndexToDeliver++;
                if (waitingToDeliver.get(nextIndexToDeliver) != null) {
                    UUID removedMessageIndex = waitingToDeliver.remove(nextIndexToDeliver);
                    deliverMessage(removedMessageIndex);
                }
            } else {
                waitingToDeliver.put(index, messageID);
            }
        } else if (index > nextIndexToDeliver) {
            waitingToDeliver.put(index, messageID);
        }
    }

    private void cabDeliver(UUID messageID) {
        listeners.forEach(cabDeliverListener -> cabDeliverListener.cabDelver(messageID));
    }

    private boolean isMajority(int replicasAcceptedNumber) {
        return replicasAcceptedNumber >= (replicasNumber * 0.5);
    }

    //upon PredicateBecomesTrue - check if any predicate becomes true due to creek message deliver
    @Override
    public synchronized void predicateBecomesTrue(int predicateId, UUID msg) {
        CabMessage nextToBeDelivered = received.get(nextIndexToDeliver);
        if (nextToBeDelivered == null) {
            return;
        }
        UUID nextToBeDeliveredUUID = nextToBeDelivered.getMessageId();
        if (msg.equals(nextToBeDeliveredUUID)) {
            deliverMessage(nextToBeDeliveredUUID);
        }
    }

    @Override
    public void registerListener(CabDeliverListener listener) {
        listeners.add(listener);
    }

    @Override
    public void start(Map<Integer, CabPredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void rbDeliver(byte[] msg) {
        Object deserialized = SerializationUtils.deserialize(msg);
        if (deserialized instanceof CabAcceptMessage) {
            acceptEventHandler((CabAcceptMessage) deserialized);
        } else if (deserialized instanceof CabBroadcastMessage) {
            broadcastEventHandler((CabBroadcastMessage) deserialized);
        } else if (deserialized instanceof CabProposeMessage) {
            proposeEventHandler((CabProposeMessage) deserialized);
        }
    }
}
