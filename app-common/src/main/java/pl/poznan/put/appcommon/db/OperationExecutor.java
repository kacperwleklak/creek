package pl.poznan.put.appcommon.db;

import pl.poznan.put.appcommon.db.request.Operation;

public interface OperationExecutor {

    void executeOperation(Operation operation, ResponseGenerator client);

}
