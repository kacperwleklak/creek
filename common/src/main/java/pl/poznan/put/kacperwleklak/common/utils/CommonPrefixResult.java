package pl.poznan.put.kacperwleklak.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class CommonPrefixResult<T> {

    private List<T> commonPrefix;
    private List<T> firstListTail;
    private List<T> secondListTail;

    public static <T> CommonPrefixResult<T> empty() {
        return new CommonPrefixResult<T>(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }
}
