package com.epam.community.middlesvc.threading;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.ObjectUtils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * <a href="https://stackoverflow.com/questions/36026402/how-to-use-mdc-with-forkjoinpool">from</a>
 * A {@link ForkJoinPool} that inherits MDC contexts from the thread that queues a task.
 *
 * @author Gili Tzabari
 */
@Slf4j
public final class MdcForkJoinPool extends ForkJoinPool {
    /**
     * Creates a new MdcForkJoinPool.
     *
     * @param parallelism the parallelism level. For default value, use {@link java.lang.Runtime#availableProcessors}.
     * @param factory     the factory for creating new threads. For default value, use
     *                    {@link #defaultForkJoinWorkerThreadFactory}.
     * @param handler     the handler for internal worker threads that terminate due to unrecoverable errors encountered
     *                    while executing tasks. For default value, use {@code null}.
     * @param asyncMode   if true, establishes local first-in-first-out scheduling mode for forked tasks that are never
     *                    joined. This mode may be more appropriate than default locally stack-based mode in applications
     *                    in which worker threads only process event-style asynchronous tasks. For default value, use
     *                    {@code false}.
     * @throws IllegalArgumentException if parallelism less than or equal to zero, or greater than implementation limit
     * @throws NullPointerException     if the factory is null
     * @throws SecurityException        if a security manager exists and the caller is not permitted to modify threads
     *                                  because it does not hold
     *                                  {@link java.lang.RuntimePermission}{@code ("modifyThread")}
     */
    public MdcForkJoinPool(final int parallelism,
                           final ForkJoinWorkerThreadFactory factory,
                           final UncaughtExceptionHandler handler,
                           final boolean asyncMode) {
        super(parallelism, factory, handler, asyncMode);
    }

    public MdcForkJoinPool(final int parallelism,
                           final ForkJoinWorkerThreadFactory factory,
                           final UncaughtExceptionHandler handler,
                           final boolean asyncMode,
                           final int corePoolSize,
                           final int maximumPoolSize,
                           final int minimumRunnable,
                           final Predicate<? super ForkJoinPool> saturate,
                           final long keepAliveTime,
                           final TimeUnit unit) {
        super(parallelism, factory, handler, asyncMode, corePoolSize, maximumPoolSize, minimumRunnable, saturate, keepAliveTime, unit);
    }

    @Override
    public void execute(final ForkJoinTask<?> task) {
        // See http://stackoverflow.com/a/19329668/14731
        super.execute(wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public void execute(final Runnable task) {
        // See http://stackoverflow.com/a/19329668/14731
        super.execute(wrap(task, MDC.getCopyOfContextMap()));
    }

    private <T> ForkJoinTask<T> wrap(final ForkJoinTask<T> task,
                                     final Map<String, String> newContext) {
        return new ForkJoinTask<T>() {
            private static final long serialVersionUID = 1L;
            /**
             * If non-null, overrides the value returned by the underlying task.
             */
            private final AtomicReference<T> override = new AtomicReference<>();

            @Override
            public T getRawResult() {
                final T result = this.override.get();
                if (result != null)
                    return result;
                return task.getRawResult();
            }

            @Override
            protected void setRawResult(final T value) {
                this.override.set(value);
            }

            @Override
            protected boolean exec() {
                // According to ForkJoinTask.fork() "it is a usage error to fork a task more than once unless it has completed
                // and been reinitialized". We therefore assume that this method does not have to be thread-safe.
                final Map<String, String> oldContext = beforeExecution(newContext);
                try {
                    task.invoke();
                    return true;
                } finally {
                    afterExecution(oldContext);
                }
            }
        };
    }

    private Runnable wrap(final Runnable task,
                          final Map<String, String> newContext) {
        return () -> {
            final Map<String, String> oldContext = beforeExecution(newContext);
            try {
                task.run();
            } finally {
                afterExecution(oldContext);
            }
        };
    }

    /**
     * Invoked before running a task.
     *
     * @param newValue the new MDC context
     * @return the old MDC context
     */
    private Map<String, String> beforeExecution(final Map<String, String> newValue) {
        final Map<String, String> previous = MDC.getCopyOfContextMap();
        if (ObjectUtils.isEmpty(newValue))
            MDC.clear();
        else
            MDC.setContextMap(newValue);
        return previous;
    }

    /**
     * Invoked after running a task.
     *
     * @param oldValue the old MDC context
     */
    private void afterExecution(final Map<String, String> oldValue) {
        if (ObjectUtils.isEmpty(oldValue))
            MDC.clear();
        else
            MDC.setContextMap(oldValue);
    }
}
