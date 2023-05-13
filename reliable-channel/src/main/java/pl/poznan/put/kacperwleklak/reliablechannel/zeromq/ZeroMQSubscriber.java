package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import lombok.extern.slf4j.Slf4j;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.function.Consumer;


@Slf4j
public class ZeroMQSubscriber implements Runnable {

    private final Consumer<byte[]> msgConsumer;
    private final ZMQ.Socket subscriber;

    public ZeroMQSubscriber(ZContext context, Consumer<byte[]> msgConsumer) {
        this.msgConsumer = msgConsumer;
        this.subscriber = context.createSocket(SocketType.SUB);
    }

    public void addPublisher(String connectionString) {
        subscriber.connect("tcp://" + connectionString);
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
    }

    @Override
    public void run() {
        while (true) {
            byte[] recv = subscriber.recv(ZMQ.SNDMORE);
            msgConsumer.accept(recv);
        }

    }
}
