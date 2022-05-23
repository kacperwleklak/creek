package pl.poznan.put.kacperwleklak.cab.message.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.poznan.put.kacperwleklak.cab.message.CabMsg;
import pl.poznan.put.kacperwleklak.cab.service.CabMessage;

import java.io.Serializable;

@ToString
public class CabBroadcastMessage extends CabMsg implements Serializable {

    @Getter
    @Setter
    private CabMessage cabMessage;

    public CabBroadcastMessage(CabMessage cabMessage) {
        super();
        this.cabMessage = cabMessage;
    }

    public CabBroadcastMessage() {
        super();
    }
}
