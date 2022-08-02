package pl.poznan.put.kacperwleklak.cab;

import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;

public interface CabPredicateCallback {

    void predicateBecomesTrue(int predicateId, CabMessageID cabMessageID);
}
