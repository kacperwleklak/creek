package pl.poznan.put.kacperwleklak.cab.predicate.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicateType;
import pl.poznan.put.kacperwleklak.creek.OperationRequest;
import pl.poznan.put.kacperwleklak.structure.IncrementalIndexList;

import java.util.List;
import java.util.stream.Collectors;

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
    public boolean isTrue(IncrementalIndexList<OperationRequest> deliveredOperations) {
        return deliveredOperations.stream()
                .map(OperationRequest::getUuid)
                .collect(Collectors.toList())
                .containsAll(messagesMustBeDelivered);
    }
}
