namespace java pl.poznan.put.kacperwleklak.redblue.protocol

struct PassToken {
    1: i8 msgType = 1,
    2: i64 redNumber,
    3: i8 recipient
}

struct Request {
    1: i8 msgType = 2,
    2: EventID requestID,
    3: i64 redNumber,
    4: Operation shadowOp,
    5: bool strongOp,
    6: set<EventID> casualCtx
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
    2: i64 currEventNo;
}