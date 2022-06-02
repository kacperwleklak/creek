package pl.poznan.put.kacperwleklak.creek.request;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class OperationRequest {

    private UUID uuid;
    private boolean isStrong;
    private Set<UUID> waitingFor;

}
