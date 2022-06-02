package pl.poznan.put.kacperwleklak.creek.message.impl;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pl.poznan.put.kacperwleklak.creek.message.CreekMsg;
import pl.poznan.put.kacperwleklak.creek.structure.Request;

import java.io.Serializable;

@Data
public class OperationRequestMessage extends CreekMsg implements Serializable {

    @Getter
    @Setter
    private Request request;

    public OperationRequestMessage(Request request) {
        super();
        this.request = request;
    }
}
