package pl.poznan.put.kacperwleklak.creek.structure.response;

import lombok.Data;
import pl.poznan.put.kacperwleklak.creek.interfaces.CreekClient;

@Data
public class ResponseHandler {

    private CreekClient client;
    private Response response;

    public ResponseHandler(CreekClient client) {
        this.client = client;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public void sendResponse() {
        CreekClient client = getClient();
        Response response = getResponse();
        client.sendResponse(response);
    }
}
