package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageType;

@JsonTypeName(AckMessage.MESSAGE_TYPE)
public class AckMessage extends CreekMsg {

    public static final String MESSAGE_TYPE = "ack";

    @Getter
    private final MessageType messageType = MessageType.ACK;

    public AckMessage() {
        super(MessageType.ACK);
    }
}
