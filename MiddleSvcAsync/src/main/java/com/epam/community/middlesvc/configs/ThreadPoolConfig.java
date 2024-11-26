package com.epam.community.middlesvc.configs;

import com.epam.community.middlesvc.threading.ExampleExecutor;
import com.epam.community.middlesvc.threading.MdcForkJoinPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    final int parallelism;
    final int corePoolSize;
    final int maxPoolSize;
    final int minRunnable;
    final int keepAliveTime;

    public ThreadPoolConfig(
            @Value("${concurrent.ForkJoinPool.parallelism:60}") int parallelism,
            @Value("${concurrent.ForkJoinPool.corePoolSize:60}") int corePoolSize,
            @Value("${concurrent.ForkJoinPool.maxPoolSize:500}") int maxPoolSize,
            @Value("${concurrent.ForkJoinPool.minRunnable:1}") int minRunnable,
            @Value("${concurrent.ForkJoinPool.keepAliveTime:60}") int keepAliveTime) {
        this.parallelism = parallelism;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.minRunnable = minRunnable;
        this.keepAliveTime = keepAliveTime;
    }

    @Bean
    @Qualifier("generalExecutor")
    public Executor generalExecutor() {
        return new ExampleExecutor(this.createMdcForkJoinPool());
    }

    private MdcForkJoinPool createMdcForkJoinPool() {
        return new MdcForkJoinPool(
                // Because RestTemplate is blocking parent thread
                parallelism,
                pool -> {
                    final ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                    thread.setName("epam-thread-" + thread.getPoolIndex());
                    thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
                    return thread;
                },
                null,
                false,
                corePoolSize,
                maxPoolSize,
                minRunnable,
                null,
                keepAliveTime,
                TimeUnit.SECONDS);
    }
}
