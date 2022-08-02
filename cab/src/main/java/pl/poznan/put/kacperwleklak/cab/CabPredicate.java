package pl.poznan.put.kacperwleklak.cab;

import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;

public interface CabPredicate {

    boolean testSync(CabMessageID messageID);
    boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback);
}
