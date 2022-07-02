package pl.poznan.put.kacperwleklak.creek.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.SerializationUtils;
import pl.poznan.put.kacperwleklak.cab.CabMessageID;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Request implements Comparable<Request>, Serializable {

    private long timestamp;
    private EventID requestID;
    private Operation operation;
    private boolean strong;
    private Set<EventID> casualCtx;

    public Request(long timestamp, EventID requestID, Operation operation, boolean strong) {
        this.timestamp = timestamp;
        this.requestID = requestID;
        this.operation = operation;
        this.strong = strong;
        this.casualCtx = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return timestamp == request.timestamp && strong == request.strong && Objects.equals(requestID, request.requestID) && Objects.equals(operation, request.operation) && Objects.equals(casualCtx, request.casualCtx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, requestID, operation, strong, casualCtx);
    }

    @Data
    @AllArgsConstructor
    public static class EventID implements CabMessageID, Comparable<EventID>, Serializable {
        private String replicaId;
        private int operationId;

        @Override
        public int compareTo(@NotNull Request.EventID o) {
            return Comparator.comparing(EventID::getReplicaId)
                    .thenComparing(EventID::getOperationId)
                    .compare(this, o);
        }
    }

    @Override
    public int compareTo(@NotNull Request o) {
        return Comparator
                .comparingLong(Request::getTimestamp)
                .thenComparing(Request::getRequestID)
                .compare(this, o);
    }
}