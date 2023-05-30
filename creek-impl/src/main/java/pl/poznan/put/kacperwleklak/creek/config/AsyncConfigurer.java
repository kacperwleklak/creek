package pl.poznan.put.kacperwleklak.creek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pl.poznan.put.kacperwleklak.creek.concurrent.RejectedExecutionHandlerImpl;

@Configuration
@EnableAsync
public class AsyncConfigurer {

    public static final String SINGLE_THREAD_EXECUTOR = "single-thread-executor";

    @Bean(SINGLE_THREAD_EXECUTOR)
    public ThreadPoolTaskExecutor singleThreadExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setQueueCapacity(Integer.MAX_VALUE);
        threadPoolTaskExecutor.setThreadNamePrefix("Creek-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new RejectedExecutionHandlerImpl());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
