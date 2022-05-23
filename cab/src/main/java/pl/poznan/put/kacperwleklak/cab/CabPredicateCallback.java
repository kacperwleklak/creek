package pl.poznan.put.kacperwleklak.cab;

import java.util.UUID;

public interface CabPredicateCallback {

    void predicateBecomesTrue(int predicateId, UUID msg);
}
