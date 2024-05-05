package com.epam.community.middlesvc.configs;

import io.micrometer.context.ContextSnapshot;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {
    private final int generalAwaitTermSecs;
    private final int generalCorePoolSize;
    private final int generalKeepAliveSecs;
    private final int generalMaxPollSize;
    private final int generalQueueCapacity;

    @Bean
    public TaskDecorator otelTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    public ExecutorConfig(@Value("${general.thread.await-term-secs:10}") final int generalAwaitTermSecs,
                          @Value("${general.thread.core-pool-size:50}") final int generalCorePoolSize,
                          @Value("${general.thread.keep-alive-secs:60}") final int generalKeepAliveSecs,
                          @Value("${general.thread.max-pool-size:100}") final int generalMaxPollSize,
                          @Value("${general.thread.queue-capacity:300}") final int generalQueueCapacity
    ) {
        this.generalAwaitTermSecs = generalAwaitTermSecs;
        this.generalCorePoolSize = generalCorePoolSize;
        this.generalKeepAliveSecs = generalKeepAliveSecs;
        this.generalMaxPollSize = generalMaxPollSize;
        this.generalQueueCapacity = generalQueueCapacity;
    }

    @Bean(name = "generalAsyncExecutor")
    public Executor securityContextExecutor(final TaskDecorator otelTaskDecorator) {
        val executor = new ThreadPoolTaskExecutor();
        executor.setAwaitTerminationSeconds(this.generalAwaitTermSecs);
        executor.setCorePoolSize(this.generalCorePoolSize);
        executor.setKeepAliveSeconds(this.generalKeepAliveSecs);
        executor.setMaxPoolSize(this.generalMaxPollSize);
        executor.setQueueCapacity(this.generalQueueCapacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("async-executor-");
        executor.setTaskDecorator(otelTaskDecorator);
        executor.initialize();

        return ContextSnapshot.captureAll().wrapExecutor(executor);
    }

}