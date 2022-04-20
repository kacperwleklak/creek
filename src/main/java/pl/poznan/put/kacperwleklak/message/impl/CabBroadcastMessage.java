package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessage;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.CreekTypedMessage;
import pl.poznan.put.kacperwleklak.message.MessageType;

@ToString
@JsonTypeName(CabBroadcastMessage.MESSAGE_TYPE)
public class CabBroadcastMessage implements CreekTypedMessage {

    public static final String MESSAGE_TYPE = "cabBroadcast";

    @Getter
    @Setter
    private CabMessage cabMessage;

    @Getter
    private final MessageType messageType = MessageType.CAB_BROADCAST_MESSAGE;

    public CabBroadcastMessage(CabMessage cabMessage) {
        this.cabMessage = cabMessage;
    }

    public CabBroadcastMessage() {}

    @Override
    public CreekMsg toCreekMsg() {
        return new CreekMsg(getMessageType(), cabMessage);
    }

    public static CabBroadcastMessage from(CreekMsg creekMsg) {
        CabMessage cabMessage = (CabMessage) creekMsg.getParameters()[1];
        return new CabBroadcastMessage(cabMessage);
    }
}
