package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import org.zeromq.*;

class ZeroMQSubscriberTest {

    //  The subscriber thread requests messages starting with
    //  A and B, then reads and counts incoming messages.
    private static class Subscriber implements ZThread.IAttachedRunnable {

        @Override
        public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {
            //  Subscribe to "A" and "B"
            ZMQ.Socket subscriber = ctx.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:6001");
            subscriber.subscribe("A".getBytes(ZMQ.CHARSET));
            subscriber.subscribe("B".getBytes(ZMQ.CHARSET));

            int count = 0;
            while (count < 5) {
                String string = subscriber.recvStr();
                if (string == null)
                    break; //  Interrupted
                count++;
                System.out.println(count);
            }
            subscriber.close();
        }
    }

    //  .split publisher thread
    //  The publisher sends random messages starting with A-J:
    private static class Publisher implements ZThread.IAttachedRunnable {
        @Override
        public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {
            ZMQ.Socket publisher = ctx.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:6000");
            while(!Thread.currentThread().isInterrupted()) {}
        }
    }

    //  .split listener thread
    //  The listener receives all messages flowing through the proxy, on its
    //  pipe. In CZMQ, the pipe is a pair of ZMQ_PAIR sockets that connect
    //  attached child threads. In other languages your mileage may vary:
    private static class Listener implements ZThread.IAttachedRunnable {
        @Override
        public void run(Object[] args, ZContext ctx, ZMQ.Socket pipe) {
            //  Print everything that arrives on pipe
            while (true) {
                ZFrame frame = ZFrame.recvFrame(pipe);
                if (frame == null)
                    break; //  Interrupted
                frame.print(null);
                frame.destroy();
            }
        }
    }

    //  .split main thread
    //  The main task starts the subscriber and publisher, and then sets
    //  itself up as a listening proxy. The listener runs as a child thread:
    public static void main(String[] argv) {
        try (ZContext ctx = new ZContext()) {
            ZThread.fork(ctx, new Publisher());
            ZThread.fork(ctx, new Subscriber());

            ZMQ.Socket subscriber = ctx.createSocket(SocketType.XSUB);
            subscriber.connect("tcp://localhost:6000");
            ZMQ.Socket publisher = ctx.createSocket(SocketType.XPUB);
            publisher.bind("tcp://*:6001");
            ZMQ.Socket listener = ZThread.fork(ctx, new Listener());
            ZMQ.proxy(subscriber, publisher, listener);

            System.out.println(" interrupted");

            // NB: child threads exit here when the context is closed
        }
    }

}