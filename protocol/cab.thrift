namespace java pl.poznan.put.kacperwleklak.cab.protocol

struct CabAcceptMessage {
    1: CabMessageID messageId,
    2: i64 sequenceNumber
}

struct CabProposeMessage {
    1: CabMessage message,
    2: i64 index,
    3: i64 sequenceNumber
}

struct CabMessageID {
    1: i8 replicaId,
    2: i64 operationId
}

struct CabMessage {
    1: CabMessageID messageID,
    2: optional i8 predicateId
}

service CabProtocol {

    oneway void acceptEventHandler(1:CabAcceptMessage acceptMessage),
    oneway void broadcastEventHandler(1:CabMessage cabMessage),
    oneway void proposeEventHandler(1:CabProposeMessage proposeMessage)

}