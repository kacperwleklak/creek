package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class ReliableChannelThrift {

    private List<ThriftClient> nodes;
    private TMultiplexedProcessor processor;
    private TSimpleServer server;

    @Autowired
    public ReliableChannelThrift(@Value("${communication.replicas.nodes}") List<String> nodesAddresses) {
        try {
            this.nodes = new ArrayList<>();
            for (String address : nodesAddresses) {
                String host = MessageUtils.hostFromAddressString(address);
                int port = MessageUtils.portFromAddressString(address);
                this.nodes.add(new ThriftClient(host, port));
            }
            this.processor = new TMultiplexedProcessor();
            TServerTransport socket = new TServerSocket(MessageUtils.myPort());
            this.server = new TSimpleServer(new TServer.Args(socket).processor(processor));
            runServer();
            log.info("Thrift server started");
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    public void registerService(String serviceName, TProcessor tproc, TServiceClientFactory clientFactory) {
        processor.registerProcessor(serviceName, tproc);
        nodes.forEach(node -> node.registerService(serviceName, clientFactory));
    }

    public void rCast(String serviceName, Consumer<TServiceClient> function) {
        nodes.forEach(thriftClient -> sendRequest(thriftClient, serviceName, function));
    }

    public void rSend(String host, int port, String serviceName, Consumer<TServiceClient> function) {
        nodes.stream()
                .filter(thriftClient -> thriftClient.getPort() == port)
                .filter(thriftClient -> thriftClient.getHost().equals(host))
                .findAny()
                .ifPresentOrElse(
                        thriftClient -> {
                            sendRequest(thriftClient, serviceName, function);
                        },
                        () -> log.error("Unable to find replica {}:{}", host, port)
                );
    }

    public void rSend(String addr, String serviceName, Consumer<TServiceClient> function) {
        String host = MessageUtils.hostFromAddressString(addr);
        int port = MessageUtils.portFromAddressString(addr);
        rSend(host, port, serviceName, function);
    }

    private void openSocketIfClosed(ThriftClient thriftClient) throws TTransportException {
        if (!thriftClient.isSocketOpened()) {
            thriftClient.openSocket();
        }
    }

    @Async
    void sendRequest(ThriftClient thriftClient, String serviceName, Consumer<TServiceClient> function) {
        try {
            openSocketIfClosed(thriftClient);
            function.accept(thriftClient.client(serviceName));
        } catch (TTransportException tTransportException) {
            log.error("Unable to open socket for {}:{}", thriftClient.getHost(), thriftClient.getPort());
        }
    }

    void runServer() {
        Runnable runnable = () -> server.serve();
        Thread serverThread = new Thread(runnable);
        serverThread.start();
    }
}
