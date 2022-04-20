package pl.poznan.put.kacperwleklak.creek;

import lombok.Data;
import pl.poznan.put.kacperwleklak.cab.predicate.CabPredicate;

@Data
public class StrongOperationRequest extends OperationRequest{

    private CabPredicate predicate;

}
