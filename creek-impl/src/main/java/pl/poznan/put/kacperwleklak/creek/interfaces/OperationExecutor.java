package pl.poznan.put.kacperwleklak.creek.interfaces;

import pl.poznan.put.kacperwleklak.creek.structure.Operation;

public interface OperationExecutor {

    void executeOperation(Operation operation, CreekClient client);

}
