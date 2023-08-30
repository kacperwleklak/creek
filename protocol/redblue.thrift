namespace java pl.poznan.put.kacperwleklak.redblue.protocol

struct PassToken {
    1: i8 msgType = 1,
    2: i64 redNumber,
    3: i8 recipient
}

struct Request {
    1: i8 msgType = 2,
    2: Dot requestID,
    3: i64 redNumber,
    4: Operation shadowOp,
    5: bool strongOp,
    6: DottedVersionVector causalCtx
}

enum Action {
    QUERY = 1,
    EXECUTE = 2
}

struct Operation {
  1: string sql,
  2: Action action
}

struct DottedVersionVector {
    1: list<i64> vc;
    2: list<set<i64>> dots;
}

struct Dot {
    1: i8 replica;
    2: i64 currEventNo;
}