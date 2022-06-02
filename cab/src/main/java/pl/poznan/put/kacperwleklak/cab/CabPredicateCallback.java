package pl.poznan.put.kacperwleklak.cab;

public interface CabPredicateCallback {

    void predicateBecomesTrue(int predicateId, CabMessageID cabMessageID);
}
