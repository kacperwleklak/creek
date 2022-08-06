package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThriftClient {

    private final String host;
    private final int port;
    private final TNonblockingTransport transport;
    private final TAsyncClientManager clientManager;
    private Map<String, TAsyncClient> clients;

    public ThriftClient(String host, int port) throws TTransportException, IOException {
        this.host = host;
        this.port = port;
        this.transport = new TNonblockingSocket(host, port);
        this.clientManager = new TAsyncClientManager();
        this.clients = new HashMap<>();
    }

    public void registerService(String serviceName, TAsyncClientFactoryBuilder clientFactoryBuilder) {
        String clientName = ThriftUtils.generateThriftServiceName(serviceName, host, port);
        clientFactoryBuilder.setClientManager(clientManager);
        clientFactoryBuilder.setProtocolFactory(new TMultiplexedProtocolFactory(clientName));
        TAsyncClientFactory clientFactory = clientFactoryBuilder.build();
        TAsyncClient client = clientFactory.getAsyncClient(transport);
        clients.put(clientName, client);
    }

    public TAsyncClient client(String serviceName) {
        return clients.get(serviceName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}