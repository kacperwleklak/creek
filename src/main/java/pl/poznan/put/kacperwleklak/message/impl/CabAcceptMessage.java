package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageType;

@ToString
@JsonTypeName(CabAcceptMessage.MESSAGE_TYPE)
public class CabAcceptMessage extends CreekMsg {

    public static final String MESSAGE_TYPE = "cabAccept";

    @Getter @Setter private String UUID;
    @Getter @Setter private int sequenceNumber;

    @Getter
    private final MessageType messageType = MessageType.CAB_ACCEPT;

    public CabAcceptMessage(String UUID, int sequenceNumber) {
        super(MessageType.CAB_ACCEPT);
        this.UUID = UUID;
        this.sequenceNumber = sequenceNumber;
    }

    public CabAcceptMessage() {
        super(MessageType.CAB_ACCEPT);
    }
}
