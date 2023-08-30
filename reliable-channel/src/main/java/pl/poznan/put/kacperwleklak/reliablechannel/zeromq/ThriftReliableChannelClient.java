package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import org.apache.thrift.TBase;

public interface ThriftReliableChannelClient {

    void rbDeliver(TBase tBase);
    TBase resolve(byte msgType);
    boolean canHandle(byte msgType);
}
