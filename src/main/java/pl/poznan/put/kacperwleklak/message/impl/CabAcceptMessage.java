package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessage;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.CreekTypedMessage;
import pl.poznan.put.kacperwleklak.message.MessageType;

@ToString
@JsonTypeName(CabAcceptMessage.MESSAGE_TYPE)
public class CabAcceptMessage implements CreekTypedMessage {

    public static final String MESSAGE_TYPE = "cabAccept";

    @Getter @Setter private String UUID;
    @Getter @Setter private int sequenceNumber;

    @Getter
    private final MessageType messageType = MessageType.CAB_ACCEPT;

    public CabAcceptMessage(String UUID, int sequenceNumber) {
        this.UUID = UUID;
        this.sequenceNumber = sequenceNumber;
    }

    public CabAcceptMessage() {}

    @Override
    public CreekMsg toCreekMsg() {
        return new CreekMsg(getMessageType(), UUID, sequenceNumber);
    }

    public static CabAcceptMessage from(CreekMsg creekMsg) {
        String UUID = (String) creekMsg.getParameters()[1];
        int sequenceNumber = (int) creekMsg.getParameters()[2];
        return new CabAcceptMessage(UUID, sequenceNumber);
    }
}
