package pl.poznan.put.kacperwleklak.cab.predicate.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicateType;

import java.util.List;

@JsonTypeName(AreMessagesDelivered.PREDICATE_NAME)
public class AreMessagesDelivered extends CabPredicate {

    public static final String PREDICATE_NAME = "ARE_MESSAGES_DELIVERED";

    @Getter
    @Setter
    private List<String> messagesMustBeDelivered;

    public AreMessagesDelivered() {
        super(CabPredicateType.ARE_MESSAGES_DELIVERED);
    }

    @Override
    public boolean isTrue(List<String> deliveredMessages) {
        return true;
    }
}
