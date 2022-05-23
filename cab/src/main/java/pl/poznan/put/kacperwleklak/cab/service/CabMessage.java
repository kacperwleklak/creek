package pl.poznan.put.kacperwleklak.cab.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

@Data
public class CabMessage implements Serializable {

    private UUID messageId;
    @EqualsAndHashCode.Exclude
    private int predicateId;

    public CabMessage(UUID messageId, int predicateId) {
        this.messageId = messageId;
        this.predicateId = predicateId;
    }

    public CabMessage() {
    }

    public CabMessage(UUID messageId) {
        this.messageId = messageId;
    }
}
