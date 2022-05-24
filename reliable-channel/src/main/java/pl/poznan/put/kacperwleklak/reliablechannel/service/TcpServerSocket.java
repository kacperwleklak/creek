package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Service
public class TcpServerSocket {

    private Selector selector;
    private ServerSocketChannel serverSocket;
    private ByteBuffer buffer;
    private Thread acceptorThread;
    private Consumer<byte[]> messageConsumer;

    public TcpServerSocket(@Value("${communication.replicas.port}") int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress("localhost", port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        buffer = ByteBuffer.allocate(2048);
        acceptorThread = new Thread(incomingConnectionsAcceptor);
        acceptorThread.start();
    }

    public void registerConsumer(Consumer<byte[]> consumer) {
        messageConsumer = consumer;
    }

    private Runnable incomingConnectionsAcceptor = new Runnable() {
        @Override
        public void run() {
            log.info("Running TcpServerSocket...");
            while (true) {
                try {
                    selector.select();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    try {
                        SelectionKey key = iter.next();
                        if (key.isAcceptable()) {
                            register(serverSocket);
                        }
                        if (key.isReadable()) {
                            consumeMessage(buffer, key);
                        }
                        iter.remove();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    };

    private void consumeMessage(ByteBuffer buffer, SelectionKey key)
            throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        byte[] msgBytes = buffer.array();
        log.debug(new String(msgBytes, StandardCharsets.UTF_8));
        if (messageConsumer != null) {
            messageConsumer.accept(msgBytes);
        }
        buffer.clear();
    }

    private void register(ServerSocketChannel serverSocket)
            throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        log.info("Registered new client {}", client.getRemoteAddress());
    }

}
