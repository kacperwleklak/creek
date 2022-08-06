package pl.poznan.put.kacperwleklak.reliablechannel.thrift;

import org.apache.thrift.async.AsyncMethodCallback;

public class DummyThriftCallback implements AsyncMethodCallback<Void> {
    @Override
    public void onComplete(Void unused) {
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
