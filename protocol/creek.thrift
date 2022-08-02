namespace java pl.poznan.put.kacperwleklak.creek.protocol

include "cab.thrift"


struct Request {
    1: i64 timestamp,
    2: EventID requestID,
    3: Operation operation,
    4: bool strong,
    5: optional set<EventID> casualCtx
}

enum Action {
    QUERY = 1,
    EXECUTE = 2
}

struct Operation {
  1: string sql,
  2: Action action
}

struct EventID {
    1: i8 replica;
    2: i64 operationId;
}

service CreekProtocol {

    oneway void operationRequestHandler(1:Request request)

}