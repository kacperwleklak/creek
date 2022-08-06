package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class ReliableChannelThrift {

    private List<ThriftClient> nodes;
    private TMultiplexedProcessor processor;
    private TNonblockingServer server;

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
            TNonblockingServerTransport socket = new TNonblockingServerSocket(MessageUtils.myPort());
            this.server = new TNonblockingServer(new TNonblockingServer.Args(socket).processor(processor));
            runServer();
            log.info("Thrift server started");
        } catch (TTransportException | IOException e) {
            e.printStackTrace();
        }
    }

    public void registerService(String serviceName, TProcessor tproc, TAsyncClientFactoryBuilder clientFactoryBuilder) {
        String myHost = MessageUtils.myHost();
        int myPort = MessageUtils.myPort();
        processor.registerProcessor(ThriftUtils.generateThriftServiceName(serviceName, myHost, myPort), tproc);
        nodes.forEach(node -> node.registerService(serviceName, clientFactoryBuilder));
    }

    public void rCast(String serviceName, Consumer<TAsyncClient> function) {
        nodes.forEach(thriftClient -> sendRequest(thriftClient, serviceName, function));
    }

    public void rSend(String host, int port, String serviceName, Consumer<TAsyncClient> function) {
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

    public void rSend(String addr, String serviceName, Consumer<TAsyncClient> function) {
        String host = MessageUtils.hostFromAddressString(addr);
        int port = MessageUtils.portFromAddressString(addr);
        rSend(host, port, serviceName, function);
    }

    void sendRequest(ThriftClient thriftClient, String serviceName, Consumer<TAsyncClient> function) {
        function.accept(thriftClient.client(serviceName));
    }

    void runServer() {
        Runnable runnable = () -> server.serve();
        Thread serverThread = new Thread(runnable);
        serverThread.start();
    }
}
