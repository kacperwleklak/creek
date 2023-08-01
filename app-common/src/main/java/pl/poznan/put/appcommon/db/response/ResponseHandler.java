package pl.poznan.put.appcommon.db.response;

import lombok.Data;
import pl.poznan.put.appcommon.db.ResponseGenerator;

@Data
public class ResponseHandler {

    private ResponseGenerator client;
    private Response response;

    public ResponseHandler(ResponseGenerator client) {
        this.client = client;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public void sendResponse() {
        ResponseGenerator client = getClient();
        Response response = getResponse();
        client.sendResponse(response);
    }
}
