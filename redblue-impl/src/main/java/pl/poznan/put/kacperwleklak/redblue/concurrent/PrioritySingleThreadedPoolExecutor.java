package pl.poznan.put.kacperwleklak.redblue.concurrent;

import java.util.concurrent.*;

public class PrioritySingleThreadedPoolExecutor extends ThreadPoolExecutor {

    private static final int N_THREADS = 1;
    private static final int QUEUE_INIT_SIZE = 10;

    public PrioritySingleThreadedPoolExecutor() {
        super(N_THREADS, N_THREADS, 0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(QUEUE_INIT_SIZE, new PriorityFutureComparator()));
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        return new PriorityFuture<T>(newTaskFor, ((PriorityCallable) callable).getPriority());
    }
}
