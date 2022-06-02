package pl.poznan.put.kacperwleklak.cab.message.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.CabMessageID;
import pl.poznan.put.kacperwleklak.cab.message.CabMsg;

import java.io.Serializable;

@ToString
public class CabAcceptMessage extends CabMsg implements Serializable {

    @Getter
    @Setter
    private CabMessageID messageID;
    @Getter
    @Setter
    private int sequenceNumber;

    public CabAcceptMessage(CabMessageID messageID, int sequenceNumber) {
        super();
        this.messageID = messageID;
        this.sequenceNumber = sequenceNumber;
    }

    public CabAcceptMessage() {
        super();
    }
}
