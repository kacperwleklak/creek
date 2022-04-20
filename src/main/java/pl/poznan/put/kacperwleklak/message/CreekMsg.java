package pl.poznan.put.kacperwleklak.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import pl.poznan.put.kacperwleklak.message.impl.CabAcceptMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabBroadcastMessage;
import pl.poznan.put.kacperwleklak.message.impl.CabProposeMessage;

@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CabProposeMessage.class, name = CabProposeMessage.MESSAGE_TYPE),
        @JsonSubTypes.Type(value = CabAcceptMessage.class, name = CabAcceptMessage.MESSAGE_TYPE),
        @JsonSubTypes.Type(value = CabBroadcastMessage.class, name = CabBroadcastMessage.MESSAGE_TYPE)
})
public class CreekMsg {

    @Getter
    private Sender sender;
    @Getter
    protected MessageType messageType;
    @Getter
    private Object[] parameters;

    public CreekMsg() {
    }

    public CreekMsg(MessageType messageType, Object... parameters) {
        this.sender = MessageUtils.generateSender();
        this.messageType = messageType;
        this.parameters = parameters;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private String host;
        private int port;
    }
}
