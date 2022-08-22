package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zeromq.*;
import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannel;
import pl.poznan.put.kacperwleklak.reliablechannel.ReliableChannelDeliverListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class ZeroMQReliableChannel implements ReliableChannel {

    private final ZMQ.Socket publisher;
    private final List<ReliableChannelDeliverListener> listeners;

    public ZeroMQReliableChannel(@Value("${communication.replicas.nodes}") List<String> replicasAddresses) {
        listeners = new ArrayList<>();
        for (String replicaAddress : replicasAddresses) {
            new Thread(new ZeroMQSubscriber(replicaAddress, messageConsumer)).start();
        }
        ZContext ctx = new ZContext();
        publisher = ctx.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:" + MessageUtils.myPort());
    }

    @Override
    public void rCast(byte[] msg) {
        synchronized (this) {
            publisher.send(msg);
        }
    }

    @Override
    public void rSend(String address, byte[] msg) {
        synchronized (this) {
            publisher.send(msg);
        }
    }

    @Override
    public void registerListener(ReliableChannelDeliverListener deliverListener) {
        listeners.add(deliverListener);
    }

    private final Consumer<byte[]> messageConsumer = new Consumer<>() {
        @Override
        public void accept(byte[] bytes) {
            synchronized(this) {
                listeners.forEach(listener -> {
                    byte msgType = -1;
                    try {
                        msgType = ThriftSerializer.getMsgType(bytes);
                        log.debug("Received message type: {}", msgType);
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                    listener.rDeliver(msgType, bytes);
                });
            }
        }
    };

}
