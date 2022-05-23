package pl.poznan.put.kacperwleklak.common.utils;

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

    public static String myAddress() {
        return toAddressString(myHost(), myPort());
    }

    public static String toAddressString(String host, int port) {
        return host + ":" + port;
    }

    public static String myHost() {
        return HOST;
    }

    public static int myPort() {
        return PORT;
    }
}
