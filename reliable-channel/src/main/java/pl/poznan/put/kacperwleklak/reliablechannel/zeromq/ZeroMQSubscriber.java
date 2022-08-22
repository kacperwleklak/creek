package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import lombok.extern.slf4j.Slf4j;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.function.Consumer;

@Slf4j
public class ZeroMQSubscriber implements Runnable {

    private final String connectionString;
    private final Consumer<byte[]> msgConsumer;

    public ZeroMQSubscriber(String connectionString, Consumer<byte[]> msgConsumer) {
        this.connectionString = connectionString;
        this.msgConsumer = msgConsumer;
    }

    @Override
    public void run() {
        ZMQ.Socket subscriber = new ZContext(1).createSocket(SocketType.SUB);
        subscriber.connect("tcp://" + connectionString);
        subscriber.subscribe("".getBytes(ZMQ.CHARSET));
        while (true) {
            byte[] recv = subscriber.recv(0);
            msgConsumer.accept(recv);
        }

    }
}
