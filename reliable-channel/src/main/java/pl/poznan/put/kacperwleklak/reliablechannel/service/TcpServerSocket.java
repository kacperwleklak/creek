package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
//@Service
public class TcpServerSocket {

    private final Selector selector;
    private final ServerSocketChannel serverSocket;
    private final Thread acceptorThread;
    private Consumer<byte[]> messageConsumer;

    @Autowired
    public TcpServerSocket(@Value("${communication.replicas.port}") int port,
                           @Value("${communication.replicas.host}") String host) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        acceptorThread = new Thread(incomingConnectionsAcceptor);
        acceptorThread.start();
    }

    public void registerConsumer(Consumer<byte[]> consumer) {
        messageConsumer = consumer;
    }

    private final Runnable incomingConnectionsAcceptor = new Runnable() {
        @Override
        public void run() {
            log.info("Running TcpServerSocket...");
            SelectionKey key;
            while (true) {
                try {
                    if (selector.select() <= 0) continue;
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectedKeys.iterator();
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            SocketChannel sc = serverSocket.accept();
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            System.out.println("Connection Accepted: " + sc.getLocalAddress());
                        }
                        if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer bb = ByteBuffer.allocate(65_536);
                            consumeMessage(bb, sc);
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    };

    private void consumeMessage(ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        socketChannel.read(buffer);
        byte[] msgBytes = buffer.array();
        if (messageConsumer != null) {
            messageConsumer.accept(msgBytes);
        }
    }

}
