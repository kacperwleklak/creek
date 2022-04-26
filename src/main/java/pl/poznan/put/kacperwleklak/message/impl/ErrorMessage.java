package pl.poznan.put.kacperwleklak.message.impl;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.message.CreekMsg;
import pl.poznan.put.kacperwleklak.message.MessageType;

@JsonTypeName(ErrorMessage.MESSAGE_TYPE)
@ToString
public class ErrorMessage extends CreekMsg {

    public static final String MESSAGE_TYPE = "error";
    @Getter
    private String reason;

    @Getter
    private final MessageType messageType = MessageType.ERROR;

    public ErrorMessage(String reason) {
        super(MessageType.ERROR);
        this.reason = reason;
    }

    public ErrorMessage(Throwable throwable) {
        super(MessageType.ERROR);
        this.reason = throwable.getMessage();
    }

    public ErrorMessage() {
        super(MessageType.ERROR);
    }
}
