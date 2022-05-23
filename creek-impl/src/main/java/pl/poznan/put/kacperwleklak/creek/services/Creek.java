package pl.poznan.put.kacperwleklak.creek.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.CabDeliverListener;
import pl.poznan.put.kacperwleklak.cab.CabPredicate;
import pl.poznan.put.kacperwleklak.creek.predicates.AreMessagesDelivered;
import pl.poznan.put.kacperwleklak.creek.requests.OperationRequest;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.common.structures.IncrementalIndexList;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class Creek implements ReliableChannelDeliverListener, CabDeliverListener {

    private final IncrementalIndexList<OperationRequest> receivedMessages;

    private final CAB cab;
    private final ReliableChannel reliableChannel;
    private final Map<Integer, CabPredicate> predicates;
    private final AreMessagesDelivered areMessagesDelivered;


    @Autowired
    public Creek(CAB cab, ReliableChannel reliableChannel,
                 AreMessagesDelivered areMessagesDelivered) {
        this.receivedMessages = new IncrementalIndexList<>();
        this.cab = cab;
        this.reliableChannel = reliableChannel;
        this.areMessagesDelivered = areMessagesDelivered;
        this.predicates = Map.of(1, areMessagesDelivered);
    }

    @PostConstruct
    public void postInitialization() {
        reliableChannel.registerListener(this);
        cab.registerListener(this);
        cab.start(predicates);
    }

    public void handleOperationRequest(OperationRequest operationRequest) {
        UUID msgUuid = operationRequest.getUuid();
        if (operationRequest.isStrong()) {
            areMessagesDelivered.registerNewMessageWaiting(msgUuid, operationRequest.getWaitingFor());
            cab.cabCast(msgUuid, 1);
        } else {
            areMessagesDelivered.registerNewDeliveredMessage(msgUuid);
        }
    }

    @Override
    public void rbDeliver(byte[] msg) {

    }

    @Override
    public void cabDelver(UUID msg) {
        log.info("CAB Delivered: {}", msg);
    }
}
