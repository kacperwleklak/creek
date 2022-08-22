package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class TcpClientSocket {

    private SocketChannel client;
    private ByteBuffer buffer;

    @Getter @Setter private String host;
    @Getter @Setter private int port;

    public TcpClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
        buffer = ByteBuffer.allocate(65_536);
    }

    public synchronized void sendMessage(byte[] message) {
        try {
            if (client == null) {
                openConnection();
            }
            buffer = ByteBuffer.wrap(message);
            int bytes = client.write(buffer);
            log.debug("Sending message to {}:{}, {} bytes", host, port, bytes);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openConnection() throws IOException {
        client = SocketChannel.open(new InetSocketAddress(host, port));
    }

}
