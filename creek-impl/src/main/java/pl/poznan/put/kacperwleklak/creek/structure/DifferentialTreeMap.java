package pl.poznan.put.kacperwleklak.creek.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DifferentialTreeMap<K, V> extends TreeMap<K, V> {

    private Map<K, V> undoMap = new HashMap<>();
    private boolean logging = false;

    public DifferentialTreeMap() {
        super();
    }

    public DifferentialTreeMap(Map<K, V> m) {
        super(m);
    }

    @Override
    public V put(K key, V value) {
        putInUndoLog(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.keySet().forEach(this::putInUndoLog);
        super.putAll(m);
    }

    private void putInUndoLog(K key) {
        if (!undoMap.containsKey(key)) {
            undoMap.put(key, get(key));
        }
    }

    public Map<K, V> dumpUndoMap() {
        logging = false;
        HashMap<K, V> undoLogCopy = new HashMap<>(undoMap);
        undoMap.clear();
        return undoLogCopy;
    }

    public void startLoggingChanges() {
        if (logging) {
            throw new RuntimeException("Started logging changes, when previous logging is not completed!");
        }
        logging = true;
        undoMap = new HashMap<>();
    }
}
