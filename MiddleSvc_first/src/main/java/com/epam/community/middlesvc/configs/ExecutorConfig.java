package com.epam.community.middlesvc.configs;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
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

/**
 * This is a configuration class for setting up the executor service.
 * It uses Spring's ThreadPoolTaskExecutor to create a thread pool.
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {
    @Value("${general.thread.await-term-secs:10}")
    private int generalAwaitTermSecs;
    @Value("${general.thread.core-pool-size:50}")
    private int generalCorePoolSize;
    @Value("${general.thread.keep-alive-secs:60}")
    private int generalKeepAliveSecs;
    @Value("${general.thread.max-pool-size:100}")
    private int generalMaxPollSize;
    @Value("${general.thread.queue-capacity:300}")
    private int generalQueueCapacity;

    /**
     * This method creates a TaskDecorator for propagating context.
     *
     * @return A TaskDecorator object.
     */
    @Bean
    public TaskDecorator otelTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    /**
     * This method creates an Executor service with a thread pool.
     * The thread pool is configured with the values defined above.
     * @param otelTaskDecorator The TaskDecorator to be used by the Executor service.
     * @return An Executor object.
     */
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

        return ContextExecutorService.wrap(executor.getThreadPoolExecutor(), ContextSnapshotFactory.builder().build()::captureAll);
    }

}