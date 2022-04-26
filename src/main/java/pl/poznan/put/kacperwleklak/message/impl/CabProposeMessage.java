package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessage;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageType;

@ToString
@JsonTypeName(CabProposeMessage.MESSAGE_TYPE)
public class CabProposeMessage extends CreekMsg {

    public static final String MESSAGE_TYPE = "cabPropose";

    @Getter @Setter private CabMessage cabMessage;
    @Getter @Setter private int index;
    @Getter @Setter private int sequenceNumber;

    @Getter
    private final MessageType messageType = MessageType.CAB_PROPOSE;

    public CabProposeMessage(CabMessage cabMessage, int index, int sequenceNumber) {
        super(MessageType.CAB_PROPOSE);
        this.cabMessage = cabMessage;
        this.index = index;
        this.sequenceNumber = sequenceNumber;
    }

    public CabProposeMessage() {
        super(MessageType.CAB_PROPOSE);
    }
}
