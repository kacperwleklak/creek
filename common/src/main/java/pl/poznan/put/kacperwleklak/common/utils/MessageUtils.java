package pl.poznan.put.kacperwleklak.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("messageUtils")
public class MessageUtils {

    @Value("${communication.replicas.host}")
    private String host;
    @Value("${communication.replicas.port}")
    private int port;

    private static String HOST_STATIC;
    private static int PORT_STATIC;

    @Value("${communication.replicas.host}")
    public void setHostStatic(String host){
        MessageUtils.HOST_STATIC = host;
    }

    @Value("${communication.replicas.port}")
    public void setPortStatic(int port){
        MessageUtils.PORT_STATIC = port;
    }

    public static String myAddress() {
        return toAddressString(myHost(), myPort());
    }

    public static String hostFromAddressString(String address) {
        return address.split(":")[0];
    }

    public static int portFromAddressString(String address) {
        try {
            return Integer.parseInt(address.split(":")[1]);
        } catch (IndexOutOfBoundsException ioobe) {
            return -1;
        }
    }

    public static String toAddressString(String host, int port) {
        return host + ":" + port;
    }

    public static String myHost() {
        return MessageUtils.HOST_STATIC;
    }

    public static int myPort() {
        return MessageUtils.PORT_STATIC;
    }
}
