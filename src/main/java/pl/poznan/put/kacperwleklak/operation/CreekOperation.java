package pl.poznan.put.kacperwleklak.operation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pl.poznan.put.kacperwleklak.operation.impl.ReadCreekOperation;
import pl.poznan.put.kacperwleklak.operation.impl.WriteCreekOperation;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "operationType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadCreekOperation.class, name = ReadCreekOperation.OPERATION_NAME),
        @JsonSubTypes.Type(value = WriteCreekOperation.class, name = WriteCreekOperation.OPERATION_NAME)
})
public abstract class CreekOperation {

    protected final CreekOperationType operationType;

    public CreekOperation(CreekOperationType operationType) {
        this.operationType = operationType;
    }

    public abstract Object execute();

    public abstract void undo();
}
