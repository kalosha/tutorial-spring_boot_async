package com.epam.community.middlesvc.threading;

import jakarta.validation.constraints.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ExampleExecutor implements Executor {

    private final ExecutorService executorService;

    public ExampleExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void execute(@NotNull final Runnable command) {
        this.executorService.execute(command);
    }
}
