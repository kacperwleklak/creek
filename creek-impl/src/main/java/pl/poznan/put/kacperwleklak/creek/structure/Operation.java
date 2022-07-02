package pl.poznan.put.kacperwleklak.creek.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Operation implements Serializable {

    private String sql;
    private Action action;

    public enum Action {
        QUERY,
        EXECUTE
    }
}
