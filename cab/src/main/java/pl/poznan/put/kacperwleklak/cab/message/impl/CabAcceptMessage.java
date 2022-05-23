package pl.poznan.put.kacperwleklak.cab.message.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.message.CabMsg;

import java.io.Serializable;
import java.util.UUID;

@ToString
public class CabAcceptMessage extends CabMsg implements Serializable {

    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private int sequenceNumber;

    public CabAcceptMessage(UUID uuid, int sequenceNumber) {
        super();
        this.uuid = uuid;
        this.sequenceNumber = sequenceNumber;
    }

    public CabAcceptMessage() {
        super();
    }
}
