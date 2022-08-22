//package pl.poznan.put.kacperwleklak.cab.service;
//
//import org.apache.thrift.TDeserializer;
//import org.apache.thrift.TException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import pl.poznan.put.kacperwleklak.cab.protocol.CabAcceptMessage;
//import pl.poznan.put.kacperwleklak.cab.protocol.CabBroadcastMessage;
//import pl.poznan.put.kacperwleklak.common.thrift.ThriftSerializer;
//
//import java.nio.charset.StandardCharsets;
//
//class CabImplTest {
//
//    @Test
//    public void test1() throws TException {
//        CabBroadcastMessage cabBroadcastMessage = new CabBroadcastMessage();
//        new TDeserializer().deserialize(cabBroadcastMessage, bytes);
//        System.out.println(cabBroadcastMessage.toString());
//    }
//
//}