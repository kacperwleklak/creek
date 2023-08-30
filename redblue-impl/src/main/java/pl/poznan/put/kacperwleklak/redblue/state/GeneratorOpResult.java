package pl.poznan.put.kacperwleklak.redblue.state;

import lombok.Data;
import pl.poznan.put.kacperwleklak.appcommon.db.response.Response;
import pl.poznan.put.kacperwleklak.redblue.protocol.Operation;

@Data
public class GeneratorOpResult {

    private static final String SEPARATOR = "|||";

    private Response response;
    private Operation shadowOp;

    public GeneratorOpResult(Response response, Operation shadowOp) {
        this.response = response;
        this.shadowOp = shadowOp;
    }

//    public static GeneratorOpResult deserialize(String serializedGeneratorOp) {
//        String[] split = serializedGeneratorOp.split(SEPARATOR);
//        return new GeneratorOpResult(split[0], split[1]);
//    }


}
