package pl.poznan.put.appcommon.db.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Request {

    private EventID eventID;
    private Operation operation;

}
