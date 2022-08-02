package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class ReliableChannelImpl implements ReliableChannel {

    private final List<String> replicasAddresses;

    private final TcpServerSocket serverSocket;
    private final List<ReliableChannelDeliverListener> listeners;
    private final Map<String, TcpClientSocket> connections;

    public ReliableChannelImpl(@Value("${communication.replicas.nodes}") List<String> replicasAddresses,
                               TcpServerSocket serverSocket) {
        this.replicasAddresses = replicasAddresses;
        this.serverSocket = serverSocket;
        this.listeners = new ArrayList<>();
        this.connections = new HashMap<>();
    }

    @PostConstruct
    private void registerHandler() {
        for (String address : replicasAddresses) {
            String host = MessageUtils.hostFromAddressString(address);
            int port = MessageUtils.portFromAddressString(address);
            connections.put(address, new TcpClientSocket(host, port));
        }
        serverSocket.registerConsumer(getMessageConsumer());
    }

    private Consumer<byte[]> getMessageConsumer() {
        return message -> listeners.forEach(listener -> {
            byte[] msgCopy = new byte[65_536];
            System.arraycopy(message, 0, msgCopy, 0, 65_536);
            listener.rDeliver(msgCopy);
        });
    }

    @Override
    public void rCast(byte[] msg) {
        connections.values().forEach(replicaTcpClient -> sendAsyncMessage(replicaTcpClient, msg));
    }

    @Override
    public void rSend(String address, byte[] msg) {
        TcpClientSocket tcpClientSocket = connections.get(address);
        if (tcpClientSocket == null) {
            String host = MessageUtils.hostFromAddressString(address);
            int port = MessageUtils.portFromAddressString(address);
            tcpClientSocket = new TcpClientSocket(host, port);
            connections.put(address, tcpClientSocket);
        }
        sendAsyncMessage(tcpClientSocket, msg);
    }

    @Override
    public void registerListener(ReliableChannelDeliverListener deliverListener) {
        listeners.add(deliverListener);
    }

    @Async
    void sendAsyncMessage(TcpClientSocket tcpClientSocket, byte[] msg) {
        tcpClientSocket.sendMessage(msg);
    }
}
