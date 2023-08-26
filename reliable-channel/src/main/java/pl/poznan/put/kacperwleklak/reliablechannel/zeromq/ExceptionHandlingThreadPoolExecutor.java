package pl.poznan.put.kacperwleklak.reliablechannel.zeromq;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ExceptionHandlingThreadPoolExecutor extends ThreadPoolExecutor {

    public ExceptionHandlingThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize,
                10 * corePoolSize,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2), new RejectedExecutionHandlerImpl());
    }

}
