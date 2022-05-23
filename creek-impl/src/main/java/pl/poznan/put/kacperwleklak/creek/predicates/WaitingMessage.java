package pl.poznan.put.kacperwleklak.creek.predicates;

import lombok.Data;
import pl.poznan.put.kacperwleklak.cab.CabPredicateCallback;

import java.util.Set;
import java.util.UUID;

@Data
public class WaitingMessage {

    private UUID uuid;
    private Set<UUID> waitingFor;
    private CabPredicateCallback callback;

    public WaitingMessage(UUID uuid, Set<UUID> waitingFor) {
        this.uuid = uuid;
        this.waitingFor = waitingFor;
    }

    public WaitingMessage() {
    }
}
