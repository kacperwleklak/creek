package pl.poznan.put.kacperwleklak.communication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.cab.CAB;
import pl.poznan.put.kacperwleklak.cab.exception.CabCastException;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageUtils;
import pl.poznan.put.kacperwleklak.message.impl.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@Slf4j
public class ReplicasMessagingService {

    private String leader;
    public boolean isLeader = false;

    private final List<String> replicasAddresses;
    private final String host;
    private final int port;

    private final TcpService tcpService;
    private CAB cab;

    @Autowired
    public ReplicasMessagingService(@Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                                    @Value("${communication.replicas.host}") String host,
                                    @Value("${communication.replicas.port}") int port,
                                    TcpService tcpService,
                                    @Lazy CAB cab) {
        this.replicasAddresses = replicasAddresses;
        this.host = host;
        this.port = port;
        this.tcpService = tcpService;
        this.cab = cab;
    }

    @PostConstruct
    private void resolveReplicas() {
        for (String address : replicasAddresses) {
            String[] addrElements = address.split(":");
            String host = addrElements[0];
            int port = Integer.parseInt(addrElements[1]);
            tcpService.registerNewServer(host, port, address, getMessageConsumer());
        }
        tcpService.setMessagesConsumer(messageHandler);
        leader = replicasAddresses.get(0);
        if (leader.equals(MessageUtils.toAddressString(host, port))) {
            isLeader = true;
            cab.setLeader(true);
        }
        cab.setReplicasNumber(replicasAddresses.size());
    }

    public void sendMessage(MessageReceiver messageReceiver, CreekMsg creekMsg) {
        String receiver;
        switch (messageReceiver) {
            case LEADER:
                receiver = leader;
                break;
            default:
                receiver = "";
        }
        sendMessage(receiver, creekMsg);
    }

    public void sendMessage(String receiver, CreekMsg creekMsg) {
        tcpService.sendMessage(receiver, creekMsg);
    }

    public void broadcastMessage(CreekMsg creekMsg) {
        tcpService.broadcastMessage(creekMsg);
    }

    private Consumer<CreekMsg> getMessageConsumer() {
        return creekMsg -> log.info("ReplicasMessagingService, received: {}", creekMsg.toString());
    }

    private final Function<CreekMsg, CreekMsg> messageHandler = creekMsg -> {
        try {
            if (creekMsg instanceof AckMessage) {
                log.debug("ACK received!");
            }
            if (creekMsg instanceof CabAcceptMessage) {
                return cab.acceptEventHandler((CabAcceptMessage) creekMsg);
            }
            if (creekMsg instanceof CabProposeMessage) {
                return cab.proposeEventHandler((CabProposeMessage) creekMsg);
            }
            if (creekMsg instanceof CabBroadcastMessage) {
                return cab.broadcastEventHandler((CabBroadcastMessage) creekMsg);
            }
        } catch (CabCastException e) {
            return new ErrorMessage(e);
        }
        return null;
    };

}
