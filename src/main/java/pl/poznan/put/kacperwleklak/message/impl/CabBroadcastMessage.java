package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessage;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageType;

@ToString
@JsonTypeName(CabBroadcastMessage.MESSAGE_TYPE)
public class CabBroadcastMessage extends CreekMsg {

    public static final String MESSAGE_TYPE = "cabBroadcast";

    @Getter
    @Setter
    private CabMessage cabMessage;

    @Getter
    private final MessageType messageType = MessageType.CAB_BROADCAST_MESSAGE;

    public CabBroadcastMessage(CabMessage cabMessage) {
        super(MessageType.CAB_BROADCAST_MESSAGE);
        this.cabMessage = cabMessage;
    }

    public CabBroadcastMessage() {
        super(MessageType.CAB_BROADCAST_MESSAGE);
    }
}
