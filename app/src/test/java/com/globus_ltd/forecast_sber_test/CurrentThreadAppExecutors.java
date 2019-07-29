package com.globus_ltd.forecast_sber_test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class CurrentThreadAppExecutors implements Config.AppExecutors {
    private ExecutorService currentThreadExecutor = new BlockingExecutor();

    @Override
    public ExecutorService getIo() {
        return currentThreadExecutor;
    }

    @Override
    public Executor getMain() {
        return currentThreadExecutor;
    }
}
