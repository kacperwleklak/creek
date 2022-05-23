package pl.poznan.put.kacperwleklak.cab.message.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.message.CabMsg;
import pl.poznan.put.kacperwleklak.cab.service.CabMessage;

import java.io.Serializable;

@ToString
public class CabProposeMessage extends CabMsg implements Serializable {


    @Getter
    @Setter
    private CabMessage cabMessage;
    @Getter
    @Setter
    private int index;
    @Getter
    @Setter
    private int sequenceNumber;

    public CabProposeMessage(CabMessage cabMessage, int index, int sequenceNumber) {
        super();
        this.cabMessage = cabMessage;
        this.index = index;
        this.sequenceNumber = sequenceNumber;
    }

    public CabProposeMessage() {
        super();
    }
}
