package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class ReliableChannelImpl implements ReliableChannel {

    private final List<String> replicasAddresses;

    private final TcpService tcpService;
    private final List<ReliableChannelDeliverListener> listeners;

    @Autowired
    public ReliableChannelImpl(@Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                               TcpService tcpService) {
        this.replicasAddresses = replicasAddresses;
        this.tcpService = tcpService;
        this.listeners = new ArrayList<>();
    }

    @PostConstruct
    private void resolveReplicas() {
        for (String address : replicasAddresses) {
            String[] addrElements = address.split(":");
            String host = addrElements[0];
            int port = Integer.parseInt(addrElements[1]);
            tcpService.registerNewServer(host, port, address);
        }
        tcpService.registerMessageHandler(getMessageConsumer());
    }

    private Consumer<byte[]> getMessageConsumer() {
        return message -> listeners.forEach(listener -> listener.rbDeliver(message));
    }

    @Override
    public void rbCast(byte[] msg) {
        tcpService.broadcastMessage(msg);
    }

    @Override
    public void rbSend(String address, byte[] msg) {
        tcpService.sendMessage(address, msg);
    }

    @Override
    public void registerListener(ReliableChannelDeliverListener deliverListener) {
        listeners.add(deliverListener);
    }
}
