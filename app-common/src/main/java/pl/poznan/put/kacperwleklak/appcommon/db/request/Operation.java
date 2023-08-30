package pl.poznan.put.kacperwleklak.appcommon.db.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Operation {

    public enum Type {
        EXECUTE,
        QUERY
    }

    private String sql;
    private Type action;

}
