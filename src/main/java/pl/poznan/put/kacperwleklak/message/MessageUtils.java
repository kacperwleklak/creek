package pl.poznan.put.kacperwleklak.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {

    private static String HOST;
    private static int PORT;

    @Value("${communication.replicas.host}")
    public void setHostStatic(String host){
        HOST = host;
    }

    @Value("${communication.replicas.port}")
    public void setPortStatic(int port){
        PORT = port;
    }

    public static CreekMsg.Sender generateSender() {
        return new CreekMsg.Sender(HOST, PORT);
    }

    public static String toAddressString(String host, int port) {
        return host + ":" + port;
    }
}
