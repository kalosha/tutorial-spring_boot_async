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
 * Configuration class for setting up the executor service.
 * This class is annotated with @Configuration to indicate that it is a source of bean definitions.
 * The @EnableAsync annotation switches on Spring’s ability to run @Async methods in a background thread pool.
 */
@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {
    @Value("${general.thread.await-term-secs:10}")
    private int generalAwaitTermSecs;
    @Value("${general.thread.core-pool-size:10}")
    private int generalCorePoolSize;
    @Value("${general.thread.keep-alive-secs:10}")
    private int generalKeepAliveSecs;
    @Value("${general.thread.max-pool-size:100}")
    private int generalMaxPollSize;
    @Value("${general.thread.queue-capacity:300}")
    private int generalQueueCapacity;

    /**
     * Bean for the OpenTelemetry task decorator.
     *
     * @return a new instance of ContextPropagatingTaskDecorator
     */
    @Bean
    public TaskDecorator otelTaskDecorator() {
        return new ContextPropagatingTaskDecorator();
    }

    /**
     * Bean for the executor service.
     * This method configures and initializes a ThreadPoolTaskExecutor with the properties defined above.
     * @param otelTaskDecorator the OpenTelemetry task decorator
     * @return an Executor wrapped with a ContextExecutorService
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

    @Bean(name = "loopAsyncExecutor_0")
    public Executor loop_0_ContextExecutor(final TaskDecorator otelTaskDecorator) {
        val executor = new ThreadPoolTaskExecutor();
        executor.setAwaitTerminationSeconds(this.generalAwaitTermSecs);
        executor.setCorePoolSize(this.generalCorePoolSize);
        executor.setKeepAliveSeconds(this.generalKeepAliveSecs);
        executor.setMaxPoolSize(this.generalMaxPollSize);
        executor.setQueueCapacity(this.generalQueueCapacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("loop-executor-0-");
        executor.setTaskDecorator(otelTaskDecorator);
        executor.initialize();

        return ContextExecutorService.wrap(executor.getThreadPoolExecutor(), ContextSnapshotFactory.builder().build()::captureAll);
    }

    @Bean(name = "loopAsyncExecutor_1")
    public Executor loop_1_ContextExecutor(final TaskDecorator otelTaskDecorator) {
        val executor = new ThreadPoolTaskExecutor();
        executor.setAwaitTerminationSeconds(this.generalAwaitTermSecs);
        executor.setCorePoolSize(this.generalCorePoolSize);
        executor.setKeepAliveSeconds(this.generalKeepAliveSecs);
        executor.setMaxPoolSize(this.generalMaxPollSize);
        executor.setQueueCapacity(this.generalQueueCapacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("loop-executor-1-");
        executor.setTaskDecorator(otelTaskDecorator);
        executor.initialize();

        return ContextExecutorService.wrap(executor.getThreadPoolExecutor(), ContextSnapshotFactory.builder().build()::captureAll);
    }

}