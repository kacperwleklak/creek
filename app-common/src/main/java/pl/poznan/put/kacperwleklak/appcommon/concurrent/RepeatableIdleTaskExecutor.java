package pl.poznan.put.kacperwleklak.appcommon.concurrent;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/***
 * This task executor allows to specify single job, that is executed when all other tasks is done
 */
@Slf4j
public class RepeatableIdleTaskExecutor extends ThreadPoolTaskExecutor {

    @Getter
    @Setter
    private Runnable idleTask;

    public RepeatableIdleTaskExecutor(Runnable idleTask) {
        super();
        this.idleTask = idleTask;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::printDiagnostics, 5, 7, TimeUnit.SECONDS);
    }

    private void printDiagnostics()  {
        log.info("queueSize={}", this.getThreadPoolExecutor().getQueue().size());
    }

    public void stopRepeating() {
        ((AlwaysFilledBlockingQueue<?>) this.getThreadPoolExecutor().getQueue()).stopFilling();
    }

    @Override
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        return new AlwaysFilledBlockingQueue<>(queueCapacity, idleTask);
    }
}
