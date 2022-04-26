package pl.poznan.put.kacperwleklak.creek;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;

@Data
public class StrongOperationRequest extends OperationRequest{

    @EqualsAndHashCode.Exclude
    private CabPredicate predicate;
}
