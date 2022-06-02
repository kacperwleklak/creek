package pl.poznan.put.kacperwleklak.creek.service;

import org.mvel2.MVEL;
import pl.poznan.put.kacperwleklak.creek.structure.DifferentialTreeMap;
import pl.poznan.put.kacperwleklak.creek.structure.Request;
import pl.poznan.put.kacperwleklak.creek.structure.Response;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class StateObject {

    private DifferentialTreeMap<String, Object> db;
    private SortedMap<Request, Map<String, Object>> undoLog;

    public StateObject() {
        db = new DifferentialTreeMap<>();
        undoLog = new TreeMap<>();
    }

    public synchronized void rollback(Request request) {
        Map<String, Object> undoMap = undoLog.get(request);
        db.putAll(undoMap);
        undoLog.remove(request);
    }

    public synchronized Response execute(Request request) {
        db.startLoggingChanges();
        Object responseObj = MVEL.executeExpression(MVEL.compileExpression(request.getOperation()), db);
        Map<String, Object> undoMap = db.dumpUndoMap();
        undoLog.put(request, undoMap);
        return new Response(responseObj);
    }
}
