package pl.poznan.put.kacperwleklak.cab.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.cab.protocol.*;
import pl.poznan.put.kacperwleklak.common.structures.IncrementalIndexList;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ConcurrentZMQChannelSupervisor;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.ThriftReliableChannelClient;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@DependsOn({"messageUtils"})
public class CabImpl implements CAB, CabPredicateCallback, ThriftReliableChannelClient {

    private static final String CAB_PROTOCOL = "CabProtocol";

    // state holders
    private final int sequenceNumber;
    private long nextIndexToDeliver;
    private Map<Integer, CabPredicate> predicates;

    // message holders
    private final IncrementalIndexList<CabMessage> received;
    private final IncrementalIndexList<CabMessageID> waitingToDeliver;
    private final ConcurrentHashMap<CabMessageID, Integer> acceptsReceived;

    private boolean isLeader = false;
    private String leaderAddr;
    private int replicasNumber;

    // communication and listeners
    private final ConcurrentZMQChannelSupervisor channelSupervisor;
    private final Set<CabDeliverListener> listeners;

    private final Object lock = new Object();

    @Autowired
    public CabImpl(ConcurrentZMQChannelSupervisor channelSupervisor,
                   @Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                   @Value("${communication.replicas.host}") String myHost,
                   @Value("${communication.replicas.port}") int myPort) {
        this.sequenceNumber = 0;
        this.nextIndexToDeliver = 0L;
        this.received = new IncrementalIndexList<>();
        this.waitingToDeliver = new IncrementalIndexList<>();
        this.acceptsReceived = new ConcurrentHashMap<>();
        this.listeners = new HashSet<>();
        this.channelSupervisor = channelSupervisor;
        setupReplicasValues(replicasAddresses, myHost, myPort);
    }

    @PostConstruct
    public void postConstruct() {
        channelSupervisor.registerListener(this);
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
        log.debug("Cab Casting: {}", messageID);
        CabMessage cabMessage = new CabMessage(messageID);
        cabMessage.setPredicateId(Integer.valueOf(predicateId).byteValue());
        CabBroadcastMessage cabBroadcastMessage = new CabBroadcastMessage(cabMessage);
        broadcast(cabBroadcastMessage);
    }

    //upon BroadcastMessage(UUIDm, q)
    public void broadcastEventHandler(CabBroadcastMessage cabBroadcastMessage) {
        synchronized (lock) {
            log.debug("Received CabBroadcastMessage: {}", cabBroadcastMessage);
            if (!isLeader) {
                return;
            }
            CabMessage cabMessage = cabBroadcastMessage.getCabMessage();
            long index = received.add(cabMessage);
            CabProposeMessage cabProposeMessage = new CabProposeMessage(cabMessage, index, sequenceNumber);
            broadcast(cabProposeMessage);
        }
    }

    //upon Propose(UUIDm, d, receivedSequenceNumber, q)
    public void proposeEventHandler(CabProposeMessage cabProposeMessage) {
        synchronized (lock) {
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
            broadcast(cabAcceptMessage);
        }
    }

    //upon Accept(UUIDm, receivedSequenceNumber)
    public void acceptEventHandler(CabAcceptMessage cabAcceptMessage) {
        synchronized (lock) {
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
    }

    private boolean isPredicateTrue(CabMessage cabMessage) {
        CabPredicate cabPredicate = predicates.get((int) cabMessage.getPredicateId());
        return cabPredicate.testAsync(cabMessage.getMessageID(), this);
    }

    private void broadcast(TBase msg) {
        channelSupervisor.rCast(msg);
    }

    private void deliverMessage(CabMessageID messageID) {
        deliverMessage(messageID, false);
    }

    private void deliverMessage(CabMessageID messageID, boolean becameTrue) {
        log.debug("CAB: Trying to deliver message {}", messageID);
        long index = received.indexOf(new CabMessage(messageID));
        if (index == nextIndexToDeliver) {
            CabMessage cabMessage = received.get(index);
            if (becameTrue || isPredicateTrue(cabMessage)) {
                nextIndexToDeliver++;
                cabDeliver(messageID);
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
    public void predicateBecomesTrue(int predicateId, CabMessageID msg) {
        log.debug("Async predicate becomes true: {}", msg);
        CabMessage nextToBeDelivered = received.get(nextIndexToDeliver);
        log.debug("nextToBeDelivered {}", nextToBeDelivered);
        if (nextToBeDelivered == null) {
            return;
        }
        CabMessageID nextToBeDeliveredID = nextToBeDelivered.getMessageID();
        if (msg.equals(nextToBeDeliveredID)) {
            deliverMessage(nextToBeDeliveredID, true);
        } else {
            log.debug("nextToBeDeliveredID {} not match current message Id {}", nextToBeDeliveredID, msg);
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
    public void rbDeliver(TBase tBase) {
        synchronized (lock) {
            if (tBase instanceof CabBroadcastMessage) {
                broadcastEventHandler((CabBroadcastMessage) tBase);
                return;
            }
            if (tBase instanceof CabAcceptMessage) {
                acceptEventHandler((CabAcceptMessage) tBase);
                return;
            }
            if (tBase instanceof CabProposeMessage) {
                proposeEventHandler((CabProposeMessage) tBase);
                return;
            }
        }
    }

    @Override
    public TBase resolve(byte msgType) {
        switch (msgType) {
            case 2: return new CabBroadcastMessage();
            case 3: return new CabAcceptMessage();
            case 4: return new CabProposeMessage();
            default: return null;
        }
    }

    @Override
    public boolean canHandle(byte msgType) {
        return msgType == 2 || msgType == 3 || msgType == 4;
    }
}