package pl.poznan.put.kacperwleklak.creek.predicates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AreMessagesDelivered implements CabPredicate {

    private static int PREDICATE_ID = 1;

    private ConcurrentHashMap<UUID, WaitingMessage> waitingMessages;
    private List<UUID> predicateBecameTrue;

    private final ReliableChannel reliableChannel;

    @Autowired
    public AreMessagesDelivered(ReliableChannel reliableChannel) {
        this.reliableChannel = reliableChannel;
        this.predicateBecameTrue = new ArrayList<>();
        this.waitingMessages = new ConcurrentHashMap<>();
    }

    public void registerNewMessageWaiting(UUID messageId, Set<UUID> messagesWaitingFor) {
        if (messagesWaitingFor.isEmpty()) {
            predicateBecameTrue.add(messageId);
            return;
        }
        WaitingMessage waitingMessage = new WaitingMessage(messageId, new HashSet<>(messagesWaitingFor));
        waitingMessages.put(messageId, waitingMessage);
    }

    public void registerNewDeliveredMessage(UUID msg) {
        List<UUID> messagesWithPredicateTrue = new ArrayList<>();
        waitingMessages.values().forEach(waitingMessage -> {
            boolean removed = waitingMessage.getWaitingFor().remove(msg);
            if (removed) {
                waitingMessage
                        .getCallback()
                        .predicateBecomesTrue(PREDICATE_ID, waitingMessage.getUuid());
                messagesWithPredicateTrue.add(waitingMessage.getUuid());
            }
        });
        predicateBecameTrue.addAll(messagesWithPredicateTrue);
        messagesWithPredicateTrue.forEach(waitingMessages::remove);
    }

    @Override
    public boolean testSync(UUID msg) {
        if (predicateBecameTrue.contains(msg)) {
            return true;
        }
        WaitingMessage waitingMessage = waitingMessages.get(msg);
        if (waitingMessage == null) {
            return false;
        }
        Set<UUID> waitingFor = waitingMessage.getWaitingFor();
        if (waitingFor == null) {
            return false;
        }
        return waitingFor.isEmpty();
    }

    @Override
    public boolean testAsync(UUID msg, CabPredicateCallback predicateCallback) {
        boolean isPredicateTrue = testSync(msg);
        if (isPredicateTrue) {
            return true;
        }
        WaitingMessage waitingMessage = waitingMessages.get(msg);
        waitingMessage.setCallback(predicateCallback);
        waitingMessages.put(msg, waitingMessage);
        return false;
    }


}
