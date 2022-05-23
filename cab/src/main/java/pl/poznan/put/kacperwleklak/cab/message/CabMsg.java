package pl.poznan.put.kacperwleklak.cab.message;

import lombok.*;
import pl.poznan.put.kacperwleklak.common.utils.MessageUtils;

import java.io.Serializable;

@ToString
public abstract class CabMsg implements Serializable {

    @Getter
    private Sender sender;

    public CabMsg() {
        this.sender = new Sender(MessageUtils.myHost(), MessageUtils.myPort());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender implements Serializable {
        private String host;
        private int port;
    }
}
