package pl.poznan.put.appcommon.concurrent;


import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.BlockingQueue;

/***
 * This task executor allows to specify single job, that is executed when all other tasks is done
 */
public class RepeatableIdleTaskExecutor extends ThreadPoolTaskExecutor {

    @Getter
    @Setter
    private Runnable idleTask;

    public RepeatableIdleTaskExecutor(Runnable idleTask) {
        super();
        this.idleTask = idleTask;
    }

    public void stopRepeating() {
        ((AlwaysFilledBlockingQueue<?>) this.getThreadPoolExecutor().getQueue()).stopFilling();
    }

    @Override
    protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
        return new AlwaysFilledBlockingQueue<>(queueCapacity, idleTask);
    }
}
