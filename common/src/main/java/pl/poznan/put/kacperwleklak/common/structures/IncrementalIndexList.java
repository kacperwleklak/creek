package pl.poznan.put.kacperwleklak.common.structures;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class IncrementalIndexList<E> extends ConcurrentHashMap<Long, E> {

    long highestIndex = -1L;

    public IncrementalIndexList() {
        super();
    }

    /**
     * Adds object to list and returns its index
     *
     * @param e object to be added
     * @return elements index
     */
    public synchronized long add(E e) {
        long index = ++highestIndex;
        put(index, e);
        return index;
    }

    public long indexOf(E e) {
        return entrySet().stream()
                .filter(integerEEntry -> integerEEntry.getValue().equals(e))
                .findFirst()
                .map(Entry::getKey)
                .orElse(-1L);
    }

    public Stream<E> stream() {
        return values().stream();
    }
}
