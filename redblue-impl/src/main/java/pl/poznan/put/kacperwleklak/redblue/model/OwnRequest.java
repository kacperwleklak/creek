package pl.poznan.put.kacperwleklak.redblue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.poznan.put.kacperwleklak.appcommon.db.ResponseGenerator;
import pl.poznan.put.kacperwleklak.redblue.protocol.Operation;

@Data
@AllArgsConstructor
public class OwnRequest {

    private ResponseGenerator client;
    private Operation operation;


}
