package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientFactory;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocolFactory;

public interface TAsyncClientFactoryBuilder {

    TAsyncClientFactoryBuilder builder();

    void setClientManager(TAsyncClientManager clientManager);

    void setProtocolFactory(TProtocolFactory protocolFactory);

    org.apache.thrift.async.TAsyncClientFactory build();
}
