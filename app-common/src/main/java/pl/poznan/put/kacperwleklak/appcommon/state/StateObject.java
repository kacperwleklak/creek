package pl.poznan.put.kacperwleklak.appcommon.state;

import pl.poznan.put.kacperwleklak.appcommon.db.request.Request;
import pl.poznan.put.kacperwleklak.appcommon.db.response.Response;

public interface StateObject {

    void rollback(Request request);

    Response execute(Request request);
}
