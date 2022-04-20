package pl.poznan.put.kacperwleklak.message;

public interface CreekTypedMessage {

    MessageType getMessageType();

    CreekMsg toCreekMsg();


}
