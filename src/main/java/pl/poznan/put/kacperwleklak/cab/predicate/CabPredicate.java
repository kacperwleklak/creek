package pl.poznan.put.kacperwleklak.cab.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import pl.poznan.put.kacperwleklak.cab.predicate.impl.AreMessagesDelivered;
import pl.poznan.put.kacperwleklak.operation.impl.ReadCreekOperation;
import pl.poznan.put.kacperwleklak.operation.impl.WriteCreekOperation;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "predicateType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AreMessagesDelivered.class, name = AreMessagesDelivered.PREDICATE_NAME),
})
public abstract class CabPredicate {

    @Getter
    private final CabPredicateType predicateType;

    public CabPredicate(CabPredicateType cabPredicateType) {
        this.predicateType = cabPredicateType;
    }

    public abstract boolean isTrue(List<String> deliveredMessages);
}
