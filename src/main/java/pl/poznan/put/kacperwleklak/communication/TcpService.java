package pl.poznan.put.kacperwleklak.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.ip.dsl.Tcp;
import org.springframework.integration.ip.tcp.serializer.TcpCodecs;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.impl.ErrorMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Component
@MessageEndpoint
public class TcpService {

    private final IntegrationFlowContext integrationFlowContext;
    private final ObjectMapper objectMapper;
    private final int timeout;

    @Autowired
    public TcpService(IntegrationFlowContext integrationFlowContext,
                      @Value("${communication.replicas.timeout}") int timeout) {
        this.integrationFlowContext = integrationFlowContext;
        this.objectMapper = new ObjectMapper();
        this.timeout = timeout;
    }

    @Setter
    private Function<CreekMsg, CreekMsg> messagesConsumer;

    @ServiceActivator(inputChannel = "fromTcp")
    public Object handleMessage(byte[] msg, MessageHeaders messageHeaders) throws JsonProcessingException {
        log.debug("Received request: {}, headers: {}", new String(msg), messageHeaders);
        CreekMsg msgToReturn;
        try {
            CreekMsg creekMsg = objectMapper.readValue(msg, CreekMsg.class);
            msgToReturn = messagesConsumer.apply(creekMsg);
        } catch (IOException e) {
            log.error("Unable to deserialize message!");
            msgToReturn = new ErrorMessage("Bad message type");
        }
        return objectMapper.writeValueAsString(msgToReturn);
    }

    public IntegrationFlowContext.IntegrationFlowRegistration registerNewServer(String host, int port, String id, Consumer<CreekMsg> messageConsumer) {
        IntegrationFlow flow = f -> f
                .handle(Tcp.outboundGateway(Tcp.netClient(host, port)
                                .serializer(TcpCodecs.crlf())
                                .deserializer(TcpCodecs.crlf()))
                        .remoteTimeout(m -> timeout))
                .transform(Transformers.fromJson(CreekMsg.class))
                .handle(messageConsumer);
        return integrationFlowContext.registration(flow).id(id).register();
    }

    @Async
    public void sendMessage(String address, CreekMsg creekMsg) {
        integrationFlowContext.getRegistrationById(address)
                .getMessagingTemplate()
                .send(generateMessage(creekMsg));
    }

    @Async
    public void broadcastMessage(CreekMsg creekMsg) {
        integrationFlowContext.getRegistry().forEach((key, value) -> {
            value.getMessagingTemplate().send(generateMessage(creekMsg));
        });
    }

    private Message<String> generateMessage(CreekMsg creekMsg) {
        return new Message<>() {
            @SneakyThrows
            @Override
            public String getPayload() {
                return new ObjectMapper().writeValueAsString(creekMsg);
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> props = new HashMap<>();
                props.put(MessageHeaders.REPLY_CHANNEL, "toTcp");
                return new MessageHeaders(props);
            }
        };
    }
}
