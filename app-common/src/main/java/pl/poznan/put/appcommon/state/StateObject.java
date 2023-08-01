package pl.poznan.put.appcommon.state;

import pl.poznan.put.appcommon.db.request.Request;
import pl.poznan.put.appcommon.db.response.Response;

public interface StateObject {

    void rollback(Request request);

    Response execute(Request request);
}
