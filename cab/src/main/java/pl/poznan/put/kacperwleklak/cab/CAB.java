package pl.poznan.put.kacperwleklak.cab;

import java.util.Map;
import java.util.UUID;

public interface CAB {

    void cabCast(UUID msg, int predicateId);
    void registerListener(CabDeliverListener  listener);
    void start(Map<Integer, CabPredicate> predicates);
}
