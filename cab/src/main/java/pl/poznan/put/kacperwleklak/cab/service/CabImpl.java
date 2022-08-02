package pl.poznan.put.kacperwleklak.cab.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.*;
import pl.poznan.put.kacperwleklak.common.structures.IncrementalIndexList;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;
import pl.poznan.put.kacperwleklak.reliablechannel.thrift.ReliableChannelThrift;
import pl.poznan.put.kacperwleklak.reliablechannel.thrift.ThriftClient;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Component
@DependsOn({"messageUtils"})
public class CabImpl implements CAB, CabPredicateCallback, CabProtocol.Iface {

    private static final String CAB_PROTOCOL = "CabProtocol";

    // state holders
    private final int sequenceNumber;
    private int nextIndexToDeliver;
    private Map<Integer, CabPredicate> predicates;

    // message holders
    private final IncrementalIndexList<CabMessage> received;
    private final IncrementalIndexList<CabMessageID> waitingToDeliver;
    private final ConcurrentHashMap<CabMessageID, Integer> acceptsReceived;

    private boolean isLeader = false;
    private String leaderAddr;
    private int replicasNumber;

    // communication and listeners
    private final ReliableChannelThrift reliableChannel;
    private final Set<CabDeliverListener> listeners;

    @Autowired
    public CabImpl(ReliableChannelThrift reliableChannel,
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
        reliableChannel.registerService(CAB_PROTOCOL,
                new CabProtocol.Processor<>(this),
                new CabProtocol.Client.Factory());
    }

    public void setupReplicasValues(List<String> replicasAddresses, String host, int port) {
        leaderAddr = replicasAddresses.get(0);
        if (MessageUtils.toAddressString(host, port).equals(leaderAddr)) {
            isLeader = true;
        }
        replicasNumber = replicasAddresses.size();
    }

    @Override
    public void cabCast(CabMessageID messageID, int predicateId) {
        CabMessage cabMessage = new CabMessage(messageID);
        cabMessage.setPredicateId(Integer.valueOf(predicateId).byteValue());
        reliableChannel.rSend(leaderAddr, CAB_PROTOCOL, broadcastMessageConsumer(cabMessage));
    }

    //upon BroadcastMessage(UUIDm, q)
    public void broadcastEventHandler(CabMessage cabMessage) {
        log.debug("Received CabBroadcastMessage: {}", cabMessage);
        if (!isLeader) {
            log.error("Unable to broadcast message. Not a leader!");
            return;
        }
        long index = received.add(cabMessage);
        CabProposeMessage cabProposeMessage = new CabProposeMessage(cabMessage, index, sequenceNumber);
        broadcast(proposeMessageConsumer(cabProposeMessage));
    }

    //upon Propose(UUIDm, d, receivedSequenceNumber, q)
    public void proposeEventHandler(CabProposeMessage cabProposeMessage) {
        log.debug("Received CabProposeMessage: {}", cabProposeMessage);
        if (cabProposeMessage.getSequenceNumber() != sequenceNumber) {
            log.error("Received proposition with invalid sequence number");
            return;
        }
        if (received.get(cabProposeMessage.getIndex()) != null && !isLeader) {
            log.error("Received proposition for value that currently exist!");
            return;
        }
        received.put(cabProposeMessage.getIndex(), cabProposeMessage.getMessage());
        CabAcceptMessage cabAcceptMessage = new CabAcceptMessage(cabProposeMessage.getMessage().getMessageID(),
                cabProposeMessage.getSequenceNumber());
        broadcast(acceptMessageConsumer(cabAcceptMessage));
    }

    //upon Accept(UUIDm, receivedSequenceNumber)
    public void acceptEventHandler(CabAcceptMessage cabAcceptMessage) {
        log.debug("Received CabAcceptMessage: {}", cabAcceptMessage);
        if (cabAcceptMessage.getSequenceNumber() != sequenceNumber) {
            log.error("Received proposition with invalid sequence number");
            return;
        }
        CabMessageID messageID = cabAcceptMessage.getMessageId();
        acceptsReceived.putIfAbsent(messageID, 0);
        Integer currentAcceptsReceived = acceptsReceived.computeIfPresent(messageID, (key, value) -> value + 1);
        if (isMajority(currentAcceptsReceived)) {
            deliverMessage(messageID);
        }
    }

    private boolean isPredicateTrue(CabMessage cabMessage) {
        CabPredicate cabPredicate = predicates.get((int) cabMessage.getPredicateId());
        return cabPredicate.testAsync(cabMessage.getMessageID(), this);
    }

    private void broadcast(Consumer<TServiceClient> clientConsumer) {
        reliableChannel.rCast(CAB_PROTOCOL, clientConsumer);
    }

    private synchronized void deliverMessage(CabMessageID messageID) {
        long index = received.indexOf(new CabMessage(messageID));
        if (index == nextIndexToDeliver) {
            CabMessage cabMessage = received.get(index);
            if (isPredicateTrue(cabMessage)) {
                cabDeliver(messageID);
                nextIndexToDeliver++;
                if (waitingToDeliver.get(nextIndexToDeliver) != null) {
                    CabMessageID removedMessageIndex = waitingToDeliver.remove(nextIndexToDeliver);
                    deliverMessage(removedMessageIndex);
                }
            } else {
                waitingToDeliver.put(index, messageID);
            }
        } else if (index > nextIndexToDeliver) {
            waitingToDeliver.put(index, messageID);
        }
    }

    private void cabDeliver(CabMessageID messageID) {
        listeners.forEach(cabDeliverListener -> cabDeliverListener.cabDelver(messageID));
    }

    private boolean isMajority(int replicasAcceptedNumber) {
        return replicasAcceptedNumber > (replicasNumber * 0.5);
    }

    //upon PredicateBecomesTrue - check if any predicate becomes true due to creek message deliver
    @Override
    public synchronized void predicateBecomesTrue(int predicateId, CabMessageID msg) {
        CabMessage nextToBeDelivered = received.get(nextIndexToDeliver);
        if (nextToBeDelivered == null) {
            return;
        }
        CabMessageID nextToBeDeliveredID = nextToBeDelivered.getMessageID();
        if (msg.equals(nextToBeDeliveredID)) {
            deliverMessage(nextToBeDeliveredID);
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

    private Consumer<TServiceClient> broadcastMessageConsumer(CabMessage cabMessage) {
        return tServiceClient -> {
            try {
                ((CabProtocol.Client) tServiceClient).broadcastEventHandler(cabMessage);
            } catch (TException e) {
                e.printStackTrace();
            }
        };
    }

    private Consumer<TServiceClient> proposeMessageConsumer(CabProposeMessage cabProposeMessage) {
        return tServiceClient -> {
            try {
                ((CabProtocol.Client) tServiceClient).proposeEventHandler(cabProposeMessage);
            } catch (TException e) {
                e.printStackTrace();
            }
        };
    }

    private Consumer<TServiceClient> acceptMessageConsumer(CabAcceptMessage acceptMessage) {
        return tServiceClient -> {
            try {
                ((CabProtocol.Client) tServiceClient).acceptEventHandler(acceptMessage);
            } catch (TException e) {
                e.printStackTrace();
            }
        };
    }
}
