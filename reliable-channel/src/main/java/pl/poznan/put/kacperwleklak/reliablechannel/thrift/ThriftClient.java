package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import java.util.HashMap;
import java.util.List;

public class ThriftClient {

    private static final String SERVICE_CLIENT_NAME_FORMAT = "%s_%s:%d";

    private final String host;
    private final int port;
    private final TSocket socket;
    private final TBinaryProtocol protocol;
    private HashMap<String, TServiceClient> clients;

    public ThriftClient(String host, int port) throws TTransportException {
        this.host = host;
        this.port = port;
        this.socket = new TSocket(host, port);
        this.protocol = new TBinaryProtocol(socket);
    }

    public void registerService(String serviceName, TServiceClientFactory clientFactory) {
        String clientName = String.format(SERVICE_CLIENT_NAME_FORMAT, serviceName, host, port);
        TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, clientName);
        clientFactory.getClient(mp);
    }

    public TServiceClient client(String serviceName) {
        return clients.get(serviceName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void openSocket() throws TTransportException {
        this.socket.open();
    }

    public boolean isSocketOpened() {
        return this.socket.isOpen();
    }
}