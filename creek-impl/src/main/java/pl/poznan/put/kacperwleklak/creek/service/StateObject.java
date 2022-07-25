package pl.poznan.put.kacperwleklak.creek.service;

import pl.poznan.put.kacperwleklak.creek.structure.Request;
import pl.poznan.put.kacperwleklak.creek.structure.response.Response;

public interface StateObject {

    void rollback(Request request);

    Response execute(Request request);
}
