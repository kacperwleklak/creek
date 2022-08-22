namespace java pl.poznan.put.kacperwleklak.cab.protocol

struct CabAcceptMessage {
    1: i8 msgType = 3,
    2: CabMessageID messageId,
    3: i64 sequenceNumber
}

struct CabProposeMessage {
    1: i8 msgType = 4,
    2: CabMessage message,
    3: i64 index,
    4: i64 sequenceNumber
}

struct CabMessageID {
    1: i8 replicaId,
    2: i64 operationId
}

struct CabMessage {
    1: CabMessageID messageID,
    2: optional i8 predicateId
}

struct CabBroadcastMessage {
    1: i8 msgType = 2,
    2: CabMessage cabMessage
}