package pl.poznan.put.kacperwleklak.operation;

import lombok.Getter;
import pl.poznan.put.kacperwleklak.operation.impl.ReadCreekOperation;
import pl.poznan.put.kacperwleklak.operation.impl.WriteCreekOperation;

public enum CreekOperationType {

    READ(ReadCreekOperation.OPERATION_NAME),
    WRITE(WriteCreekOperation.OPERATION_NAME);

    @Getter
    private final String operation;

    CreekOperationType(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation;
    }
}
