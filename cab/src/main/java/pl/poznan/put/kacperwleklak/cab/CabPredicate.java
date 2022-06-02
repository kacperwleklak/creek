package pl.poznan.put.kacperwleklak.cab;

public interface CabPredicate {

    boolean testSync(CabMessageID messageID);
    boolean testAsync(CabMessageID messageID, CabPredicateCallback predicateCallback);
}
