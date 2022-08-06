package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

public class TMultiplexedProtocolFactory implements TProtocolFactory {

    private final String clientName;

    public TMultiplexedProtocolFactory(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public TProtocol getProtocol(TTransport tTransport) {
        return new TMultiplexedProtocol(new TBinaryProtocol(tTransport), clientName);
    }
}
