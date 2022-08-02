package pl.poznan.put.kacperwleklak.cab;

import pl.poznan.put.kacperwleklak.cab.protocol.CabMessageID;

import java.util.Map;

public interface CAB {

    void cabCast(CabMessageID messageID, int predicateId);
    void registerListener(CabDeliverListener  listener);
    void start(Map<Integer, CabPredicate> predicates);
}
