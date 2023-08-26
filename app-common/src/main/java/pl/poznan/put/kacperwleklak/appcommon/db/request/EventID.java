package pl.poznan.put.kacperwleklak.appcommon.db.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class EventID implements Comparable<EventID> {

    private short replica;
    private long operationId;

    @Override
    public int compareTo(@NotNull EventID other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = Boolean.compare(replica != 0, other.replica != 0);
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (replica != 0) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.replica, other.replica);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.compare(operationId != 0, other.operationId != 0);
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (operationId != 0) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.operationId, other.operationId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
}
