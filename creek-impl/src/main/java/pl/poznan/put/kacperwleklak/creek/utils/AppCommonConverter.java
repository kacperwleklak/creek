package pl.poznan.put.kacperwleklak.creek.utils;

import lombok.experimental.UtilityClass;
import pl.poznan.put.appcommon.db.request.EventID;
import pl.poznan.put.appcommon.db.request.Request;
import pl.poznan.put.kacperwleklak.creek.protocol.Action;
import pl.poznan.put.kacperwleklak.creek.protocol.Operation;

@UtilityClass
public class AppCommonConverter {

    public static Request toAppCommonRequest(pl.poznan.put.kacperwleklak.creek.protocol.Request creekRequest) {
        return new Request(toAppCommonEventId(creekRequest.getRequestID()), toAppCommonOperation(creekRequest.getOperation()));
    }

    public static Operation fromAppCommonOperation(pl.poznan.put.appcommon.db.request.Operation operation) {
        return new Operation(operation.getSql(), resolveOperationType(operation.getAction()));
    }

    public static pl.poznan.put.appcommon.db.request.Operation toAppCommonOperation(Operation operation) {
        return new pl.poznan.put.appcommon.db.request.Operation(operation.getSql(), resolveOperationType(operation.getAction()));
    }

    public static EventID toAppCommonEventId(pl.poznan.put.kacperwleklak.creek.protocol.EventID eventID) {
        return new EventID(eventID.getReplica(), eventID.getOperationId());
    }

    private static Action resolveOperationType(pl.poznan.put.appcommon.db.request.Operation.Type type) {
        switch (type) {
            case QUERY:
                return Action.QUERY;
            case EXECUTE:
                return Action.EXECUTE;
            default:
                throw new ClassCastException("Unable to resolve operation type");
        }
    }

    private static pl.poznan.put.appcommon.db.request.Operation.Type resolveOperationType(Action action) {
        switch (action) {
            case QUERY:
                return pl.poznan.put.appcommon.db.request.Operation.Type.QUERY;
            case EXECUTE:
                return pl.poznan.put.appcommon.db.request.Operation.Type.EXECUTE;
            default:
                throw new ClassCastException("Unable to resolve operation type");
        }
    }

}
