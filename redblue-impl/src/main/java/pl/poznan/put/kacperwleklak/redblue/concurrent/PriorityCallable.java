package pl.poznan.put.kacperwleklak.redblue.concurrent;

import java.util.concurrent.Callable;

public class PriorityCallable implements Callable<Void> {

    private int priority;
    private long secondPriority;
    private Callable<Void> callable;

    public PriorityCallable(int priority, Callable<Void> callable) {
        this.priority = priority;
        this.callable = callable;
    }

    public PriorityCallable(int priority, Runnable callable) {
        this.priority = priority;
        this.secondPriority = 0;
        this.callable = () -> {
            callable.run();
            return null;
        };
    }

    public PriorityCallable(int priority, long secondPriority, Runnable callable) {
        this.priority = priority;
        this.secondPriority = secondPriority;
        this.callable = () -> {
            callable.run();
            return null;
        };
    }

    @Override
    public Void call() throws Exception {
        return callable.call();
    }


    public int getPriority() {
        return priority;
    }

    public long getSecondPriorityPriority() {
        return secondPriority;
    }
}
