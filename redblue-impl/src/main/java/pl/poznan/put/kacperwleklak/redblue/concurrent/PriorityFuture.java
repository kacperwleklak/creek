package pl.poznan.put.kacperwleklak.redblue.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PriorityFuture<T> implements RunnableFuture<T> {

    private RunnableFuture<T> src;
    private int priority;
    private long secondPriority;

    public PriorityFuture(RunnableFuture<T> other, int priority, long secondPriority) {
        this.src = other;
        this.priority = priority;
        this.secondPriority = secondPriority;
    }

    public int getPriority() {
        return priority;
    }
    public long getSecondPriority() {
        return secondPriority;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return src.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return src.isCancelled();
    }

    public boolean isDone() {
        return src.isDone();
    }

    public T get() throws InterruptedException, ExecutionException {
        return src.get();
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return src.get();
    }

    public void run() {
        try {
            src.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}