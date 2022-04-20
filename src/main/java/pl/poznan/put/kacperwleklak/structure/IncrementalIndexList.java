package pl.poznan.put.kacperwleklak.structure;

import java.util.concurrent.ConcurrentHashMap;

public class IncrementalIndexList<E> extends ConcurrentHashMap<Integer, E> {

    int highestIndex = -1;

    public IncrementalIndexList() {
        super();
    }

    /**
     * Adds object to list and returns its index
     *
     * @param e object to be added
     * @return elements index
     */
    public synchronized int add(E e) {
        int index = ++highestIndex;
        put(index, e);
        return index;
    }

    public int indexOf(E e) {
        return entrySet().stream()
                .filter(integerEEntry -> integerEEntry.getValue().equals(e))
                .findFirst()
                .map(Entry::getKey)
                .orElse(-1);
    }
}
