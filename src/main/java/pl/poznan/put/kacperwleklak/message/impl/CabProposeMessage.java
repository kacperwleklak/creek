package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessage;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.CreekTypedMessage;
import pl.poznan.put.kacperwleklak.message.MessageType;
import pl.poznan.put.kacperwleklak.operation.impl.ReadCreekOperation;

@ToString
@JsonTypeName(CabProposeMessage.MESSAGE_TYPE)
public class CabProposeMessage implements CreekTypedMessage {

    public static final String MESSAGE_TYPE = "cabPropose";

    @Getter @Setter private CabMessage cabMessage;
    @Getter @Setter private int index;
    @Getter @Setter private int sequenceNumber;

    @Getter
    private final MessageType messageType = MessageType.CAB_PROPOSE;

    public CabProposeMessage(CabMessage cabMessage, int index, int sequenceNumber) {
        this.cabMessage = cabMessage;
        this.index = index;
        this.sequenceNumber = sequenceNumber;
    }

    public CabProposeMessage() {}

    @Override
    public CreekMsg toCreekMsg() {
        return new CreekMsg(getMessageType(), cabMessage, index, sequenceNumber);
    }

    public static CabProposeMessage from(CreekMsg creekMsg) {
        CabMessage cabMessage = (CabMessage) creekMsg.getParameters()[1];
        int index = (int) creekMsg.getParameters()[2];
        int sequenceNumber = (int) creekMsg.getParameters()[3];
        return new CabProposeMessage(cabMessage, index, sequenceNumber);
    }
}
