package pl.poznan.put.kacperwleklak.creek.operation;

import pl.poznan.put.kacperwleklak.creek.operation.impl.ReadCreekOperation;
import pl.poznan.put.kacperwleklak.creek.operation.impl.WriteCreekOperation;
import lombok.Getter;

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
