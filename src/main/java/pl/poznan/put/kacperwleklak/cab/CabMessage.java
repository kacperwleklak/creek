package pl.poznan.put.kacperwleklak.cab;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;

@Data
public class CabMessage {

    private String messageId;
    @EqualsAndHashCode.Exclude
    private CabPredicate predicate;

    public CabMessage(String messageId, CabPredicate predicate) {
        this.messageId = messageId;
        this.predicate = predicate;
    }

    public CabMessage() {
    }

    public String getMessageId() {
        return messageId;
    }

    public CabPredicate getPredicate() {
        return predicate;
    }
}
