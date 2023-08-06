package pl.poznan.put.kacperwleklak.appcommon.db.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventID {

    private short replica;
    private long operationId;

}
