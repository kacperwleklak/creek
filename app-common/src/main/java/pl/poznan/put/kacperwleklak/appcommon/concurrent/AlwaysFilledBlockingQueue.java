package pl.poznan.put.kacperwleklak.appcommon.concurrent;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AlwaysFilledBlockingQueue<E> extends LinkedBlockingQueue<E> {

    private boolean keepFilling = true;
    @Setter
    private E filling;

    public AlwaysFilledBlockingQueue(boolean keepFilling) {
        this.keepFilling = keepFilling;
    }

    public AlwaysFilledBlockingQueue(int capacity, E filling) {
        super(capacity);
        this.filling = filling;
    }

    public void stopFilling() {
        this.keepFilling = false;
    }

    public void startFilling() {
        this.keepFilling = true;
    }

    private void fill() {
        if (isEmpty() && keepFilling) {
            log.debug("inserted filling");
            super.offer(filling);
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        boolean offer = super.offer(e, timeout, unit);
        startFilling();
        return offer;
    }

    @Override
    public boolean offer(@NotNull E e) {
        boolean offer = super.offer(e);
        startFilling();
        return offer;
    }

    @Override
    public void put(E e) throws InterruptedException {
        super.put(e);
        startFilling();
    }

    @Override
    public E take() throws InterruptedException {
        E take = super.take();
        fill();
        return take;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E poll = super.poll(timeout, unit);
        fill();
        return poll;
    }

    @Override
    public E poll() {
        E poll = super.poll();
        fill();
        return poll;
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e);
        startFilling();
        return add;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean addAll = super.addAll(c);
        startFilling();
        return addAll;
    }
}
