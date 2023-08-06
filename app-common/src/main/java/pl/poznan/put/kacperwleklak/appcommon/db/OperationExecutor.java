package pl.poznan.put.kacperwleklak.appcommon.db;

import pl.poznan.put.kacperwleklak.appcommon.db.request.Operation;

public interface OperationExecutor {

    void executeOperation(Operation operation, ResponseGenerator client);

}
