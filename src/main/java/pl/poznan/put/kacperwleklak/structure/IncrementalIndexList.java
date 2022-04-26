package pl.poznan.put.kacperwleklak.structure;

import pl.poznan.put.kacperwleklak.creek.OperationRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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

    public Stream<E> stream() {
        return values().stream();
    }
}
