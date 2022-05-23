package pl.poznan.put.kacperwleklak.creek.operation.impl;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import pl.poznan.put.kacperwleklak.creek.operation.CreekOperation;
import pl.poznan.put.kacperwleklak.creek.operation.CreekOperationType;

@JsonTypeName(ReadCreekOperation.OPERATION_NAME)
public class ReadCreekOperation extends CreekOperation {

    public static final String OPERATION_NAME = "read";

    @Getter
    private String key;

    public ReadCreekOperation() {
        super(CreekOperationType.READ);
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public void undo() {}
}
