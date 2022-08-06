package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ThriftUtils {

    private static final String SERVICE_CLIENT_NAME_FORMAT = "%s-%s-%d"; // name-host-port

    public String generateThriftServiceName(String name, String host, int port) {
        return String.format(name, host, port);
    }
}
