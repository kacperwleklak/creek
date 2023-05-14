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

    private final ZContext ctx;
    private final ZMQ.Socket publisher;
    private final List<ReliableChannelDeliverListener> listeners = new ArrayList<>();

    public ZeroMQReliableChannel(@Value("${communication.replicas.nodes}") List<String> replicasAddresses) {
        ctx = new ZContext();
        ZeroMQSubscriber subscriber = new ZeroMQSubscriber(ctx, messageConsumer);
        for (String replicaAddress : replicasAddresses) {
            subscriber.addPublisher(replicaAddress);
        }
        new Thread(subscriber, "zmq-sub").start();
        publisher = ctx.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:" + MessageUtils.myPort());
    }

    @Override
    public void rCast(byte[] msg) {
        publisher.send(msg);
    }

    @Override
    public void rSend(String address, byte[] msg) {
        publisher.send(msg);
    }

    @Override
    public void registerListener(ReliableChannelDeliverListener deliverListener) {
        listeners.add(deliverListener);
    }

    private final Consumer<byte[]> messageConsumer = (bytes -> {
        byte msgType = -1;
        try {
            msgType = ThriftSerializer.getMsgType(bytes);
            log.debug("Received message type: {}", msgType);
        } catch (TException e) {
            e.printStackTrace();
        }
        byte finalMsgType = msgType;
        listeners.forEach(listener -> listener.rDeliver(finalMsgType, bytes));
    });

}
