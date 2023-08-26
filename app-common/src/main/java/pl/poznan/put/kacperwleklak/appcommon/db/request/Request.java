package pl.poznan.put.kacperwleklak.appcommon.db.request;

import lombok.Data;

@Data
public class Request implements Comparable<Request> {

    public Request(EventID eventID, Operation operation) {
        this.eventID = eventID;
        this.operation = operation;
    }

    public Request(EventID eventID, Operation operation, Long timestamp) {
        this.eventID = eventID;
        this.operation = operation;
        this.timestamp = timestamp;
    }

    private EventID eventID;
    private Operation operation;
    private Long timestamp;

    public int compareTo(Request other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        if (timestamp != null) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.timestamp, other.timestamp);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.compare(timestamp != null, other.timestamp != null);
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (eventID != null) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(eventID, other.eventID);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.compare(eventID != null, other.eventID != null);
        return lastComparison;
    }
}
