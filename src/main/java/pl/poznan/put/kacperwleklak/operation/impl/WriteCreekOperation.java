package pl.poznan.put.kacperwleklak.operation.impl;


import com.fasterxml.jackson.annotation.JsonTypeName;
import pl.poznan.put.kacperwleklak.operation.CreekOperation;
import pl.poznan.put.kacperwleklak.operation.CreekOperationType;

@JsonTypeName(WriteCreekOperation.OPERATION_NAME)
public class WriteCreekOperation extends CreekOperation {

    public static final String OPERATION_NAME = "write";

    private String key;
    private String value;

    public WriteCreekOperation() {
        super(CreekOperationType.WRITE);
    }

    @Override
    public Object execute() {
        return null;
    }

    @Override
    public void undo() {

    }
}
