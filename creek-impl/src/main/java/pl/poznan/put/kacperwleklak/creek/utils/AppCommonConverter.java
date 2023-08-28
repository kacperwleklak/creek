package pl.poznan.put.kacperwleklak.creek.utils;

import lombok.experimental.UtilityClass;
import pl.poznan.put.kacperwleklak.appcommon.db.request.EventID;
import pl.poznan.put.kacperwleklak.appcommon.db.request.Request;
import pl.poznan.put.kacperwleklak.creek.protocol.Action;
import pl.poznan.put.kacperwleklak.creek.protocol.Operation;

@UtilityClass
public class AppCommonConverter {

    public static Request toAppCommonRequest(pl.poznan.put.kacperwleklak.creek.protocol.Request creekRequest) {
        return new Request(toAppCommonEventId(creekRequest.getRequestID()), toAppCommonOperation(creekRequest.getOperation()), creekRequest.getTimestamp());
    }

    public static Operation fromAppCommonOperation(pl.poznan.put.kacperwleklak.appcommon.db.request.Operation operation) {
        return new Operation(operation.getSql(), resolveOperationType(operation.getAction()));
    }

    public static pl.poznan.put.kacperwleklak.appcommon.db.request.Operation toAppCommonOperation(Operation operation) {
        return new pl.poznan.put.kacperwleklak.appcommon.db.request.Operation(operation.getSql(), resolveOperationType(operation.getAction()));
    }

    public static EventID toAppCommonEventId(pl.poznan.put.kacperwleklak.creek.protocol.Dot dot) {
        return new EventID(dot.getReplica(), dot.getCurrEventNo());
    }

    private static Action resolveOperationType(pl.poznan.put.kacperwleklak.appcommon.db.request.Operation.Type type) {
        switch (type) {
            case QUERY:
                return Action.QUERY;
            case EXECUTE:
                return Action.EXECUTE;
            default:
                throw new ClassCastException("Unable to resolve operation type");
        }
    }

    private static pl.poznan.put.kacperwleklak.appcommon.db.request.Operation.Type resolveOperationType(Action action) {
        switch (action) {
            case QUERY:
                return pl.poznan.put.kacperwleklak.appcommon.db.request.Operation.Type.QUERY;
            case EXECUTE:
                return pl.poznan.put.kacperwleklak.appcommon.db.request.Operation.Type.EXECUTE;
            default:
                throw new ClassCastException("Unable to resolve operation type");
        }
    }

}
