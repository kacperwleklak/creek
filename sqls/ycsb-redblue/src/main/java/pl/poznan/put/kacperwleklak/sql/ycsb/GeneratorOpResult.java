package pl.poznan.put.kacperwleklak.sql.ycsb;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneratorOpResult {

    private static final String SEPARATOR = "~~~";
    private static final String FORMAT = "%s%s%s%s";


    private String shadowOp;
    private String result;

    public String serialize() {
        // expected result = "<protocol_trash>~~~UPDATE x SET y WHERE z~~~LOWER CASE"
        return String.format(FORMAT, SEPARATOR, shadowOp, SEPARATOR, result);
    }
}
