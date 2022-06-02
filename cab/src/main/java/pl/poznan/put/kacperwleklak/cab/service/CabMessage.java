package pl.poznan.put.kacperwleklak.cab.service;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.poznan.put.kacperwleklak.cab.CabMessageID;

import java.io.Serializable;

@Data
public class CabMessage implements Serializable {

    private CabMessageID messageID;
    @EqualsAndHashCode.Exclude
    private int predicateId;

    public CabMessage(CabMessageID messageID, int predicateId) {
        this.messageID = messageID;
        this.predicateId = predicateId;
    }

    public CabMessage() {
    }

    public CabMessage(CabMessageID messageID) {
        this.messageID = messageID;
    }
}
