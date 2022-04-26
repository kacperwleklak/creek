package pl.poznan.put.kacperwleklak.cab.predicate;

import lombok.Getter;
import pl.poznan.put.kacperwleklak.cab.predicate.impl.AreMessagesDelivered;

public enum CabPredicateType {

    ARE_MESSAGES_DELIVERED(AreMessagesDelivered.PREDICATE_NAME);

    CabPredicateType(String predicateName) {
        this.predicateName = predicateName;
    }

    @Getter
    private final String predicateName;
}
