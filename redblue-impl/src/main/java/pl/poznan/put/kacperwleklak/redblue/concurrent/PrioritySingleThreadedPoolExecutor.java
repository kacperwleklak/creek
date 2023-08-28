package pl.poznan.put.kacperwleklak.redblue.concurrent;

import lombok.extern.slf4j.Slf4j;
import pl.poznan.put.kacperwleklak.reliablechannel.zeromq.RejectedExecutionHandlerImpl;

import java.util.concurrent.*;

@Slf4j
public class PrioritySingleThreadedPoolExecutor extends ThreadPoolExecutor {

    private static final int N_THREADS = 1;

    public PrioritySingleThreadedPoolExecutor() {
        super(N_THREADS,
                Integer.MAX_VALUE / 3,
                0L, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(Integer.MAX_VALUE / 3, new PriorityFutureComparator()),
                new RejectedExecutionHandlerImpl());
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::printDiagnostics, 20, 10, TimeUnit.SECONDS);
    }

    private void printDiagnostics() {
        log.info("queueLength={}", getQueue().size());
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        RunnableFuture<T> newTaskFor = super.newTaskFor(callable);
        return new PriorityFuture<T>(newTaskFor, ((PriorityCallable) callable).getPriority(),
                ((PriorityCallable) callable).getSecondPriorityPriority());
    }

    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
           t.printStackTrace();
        }
    }
}
