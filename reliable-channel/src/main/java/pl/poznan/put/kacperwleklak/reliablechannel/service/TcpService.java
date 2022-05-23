package pl.poznan.put.kacperwleklak.reliablechannel.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@MessageEndpoint
public class TcpService {

    private final IntegrationFlowContext integrationFlowContext;
    private final int timeout;

    private Consumer<byte[]> messageHandler;

    @Autowired
    public TcpService(IntegrationFlowContext integrationFlowContext,
                      @Value("${communication.replicas.timeout}") int timeout) {
        this.integrationFlowContext = integrationFlowContext;
        this.timeout = timeout;
    }

    @ServiceActivator(inputChannel = "fromTcp")
    public byte[] handleMessage(byte[] msg, MessageHeaders messageHeaders) {
        log.debug("Received TCP request: {}, headers: {}", new String(msg), messageHeaders);
        messageHandler.accept(msg);
        return new byte[0];
    }

    private final Function<byte[], byte[]> consumer = bytes -> new byte[]{10};

    public IntegrationFlowContext.IntegrationFlowRegistration registerNewServer(String host, int port, String id) {
        IntegrationFlow flow = f -> f
                .handle(Tcp.outboundGateway(Tcp.netClient(host, port)
                                .serializer(new ByteArrayLfSerializer())
                                .deserializer(new ByteArrayLfSerializer()))
                        .remoteTimeout(m -> timeout))
                .handle(consumer);
        return integrationFlowContext.registration(flow).id(id).register();
    }

    @Async
    public void sendMessage(String address, byte[] msg) {
        integrationFlowContext.getRegistrationById(address)
                .getMessagingTemplate()
                .send(generateMessage(msg));
    }

    public void registerMessageHandler(Consumer<byte[]> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Async
    public void broadcastMessage(byte[] msg) {
        integrationFlowContext.getRegistry().forEach((key, value) -> value.getMessagingTemplate().send(generateMessage(msg)));
    }

    private Message<byte[]> generateMessage(byte[] msg) {
        return new Message<>() {
            @SneakyThrows
            @Override
            public byte[] getPayload() {
                return appendTerminationMark(msg);
            }

            private byte[] appendTerminationMark(byte[] array) {
                byte[] result = new byte[array.length + 1];
                System.arraycopy(array, 0, result, 0, array.length);
                result[result.length - 1] = 10;
                return result;
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> props = new HashMap<>();
                props.put(MessageHeaders.REPLY_CHANNEL, "fromTcp");
                return new MessageHeaders(props);
            }
        };
    }
}
