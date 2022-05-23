package pl.poznan.put.kacperwleklak.cab;

import java.util.UUID;

public interface CabPredicate {

    boolean testSync(UUID msg);
    boolean testAsync(UUID msg, CabPredicateCallback predicateCallback);
}
