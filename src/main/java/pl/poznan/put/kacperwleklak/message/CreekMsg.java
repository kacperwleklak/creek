package pl.poznan.put.kacperwleklak.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import pl.poznan.put.kacperwleklak.message.impl.*;

@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AckMessage.class, name = AckMessage.MESSAGE_TYPE),
        @JsonSubTypes.Type(value = CabProposeMessage.class, name = CabProposeMessage.MESSAGE_TYPE),
        @JsonSubTypes.Type(value = CabAcceptMessage.class, name = CabAcceptMessage.MESSAGE_TYPE),
        @JsonSubTypes.Type(value = CabBroadcastMessage.class, name = CabBroadcastMessage.MESSAGE_TYPE),

        @JsonSubTypes.Type(value = ErrorMessage.class, name = ErrorMessage.MESSAGE_TYPE)
})
public abstract class CreekMsg {

    @Getter
    private Sender sender;
    @Getter
    protected MessageType messageType;

    public CreekMsg() {
    }

    public CreekMsg(MessageType messageType) {
        this.sender = MessageUtils.generateSender();
        this.messageType = messageType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {
        private String host;
        private int port;
    }
}
