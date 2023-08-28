namespace java pl.poznan.put.kacperwleklak.creek.protocol

include "cab.thrift"

struct Request {
    1: i8 msgType = 1,
    2: i64 timestamp,
    3: Dot requestID,
    4: Operation operation,
    5: bool strong,
    6: optional DottedVersionVector causalCtx
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