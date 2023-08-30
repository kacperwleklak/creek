package pl.poznan.put.kacperwleklak.appcommon.db.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Response {

    private List<ResponseMessageStream> responseMessageList = new ArrayList<>();

    public void addMessage(ResponseMessageStream responseMessage) {
        responseMessageList.add(responseMessage);
    }

}
