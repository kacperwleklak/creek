package pl.poznan.put.kacperwleklak.creek.service;


import pl.poznan.put.appcommon.db.PostgresServer;

import pl.poznan.put.appcommon.db.response.Response;
import pl.poznan.put.appcommon.state.StateObjectSql;
import pl.poznan.put.kacperwleklak.creek.protocol.Request;
import pl.poznan.put.kacperwleklak.creek.utils.AppCommonConverter;

public class StateObjectAdapter extends StateObjectSql {

    public StateObjectAdapter(PostgresServer pgServer) {
        super(pgServer);
    }

    public void rollback(Request request) {
        super.rollback(AppCommonConverter.toAppCommonRequest(request));
    }

    public Response execute(Request request) {
        return super.execute(AppCommonConverter.toAppCommonRequest(request));
    }
}
