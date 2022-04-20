package pl.poznan.put.kacperwleklak.operation.impl;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import pl.poznan.put.kacperwleklak.operation.CreekOperation;
import pl.poznan.put.kacperwleklak.operation.CreekOperationType;

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
