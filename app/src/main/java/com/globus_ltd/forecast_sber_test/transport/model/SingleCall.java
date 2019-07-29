package com.globus_ltd.forecast_sber_test.transport.model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SingleCall<T> implements Call<T> {
    private Callable<T> task;
    private ExecutorService backgroundExecutor;
    private Set<Callback<T>> callbacks = new HashSet<>();
    private Future<?> resultFuture;
    private boolean canceled;
    private boolean executed;

    public SingleCall(Callable<T> task, ExecutorService backgroundExecutor) {
        this.task = task;
        this.backgroundExecutor = backgroundExecutor;
    }

    @Override
    public void run() {
        if (executed || canceled) {
            return;
        }
        synchronized (this) {
            if (executed || canceled) {
                return;
            }
            executed = true;
            resultFuture = backgroundExecutor.submit(new CallbackableRunnable());
        }
    }

    @Override
    public void cancel() {
        if (canceled) {
            return;
        }
        synchronized (this) {
            canceled = true;
            if (resultFuture != null && !resultFuture.isDone()) {
                resultFuture.cancel(true);
            }
        }
    }

    private void resultSuccess(T data) {
        synchronized (this) {
            for (Callback<T> callback : callbacks) {
                callback.onSuccess(data);
            }
            callbacks.clear();
        }
    }

    private void resultFailed(Throwable throwable) {
        synchronized (this) {
            for (Callback<T> callback : callbacks) {
                callback.onError(throwable);
            }
            callbacks.clear();
        }
    }

    @Override
    public void subscribe(Callback<T> callback) {
        synchronized (this) {
            if (executed) {
                callback.onError(new RuntimeException("Already executed"));
                return;
            }
            callbacks.add(callback);
        }
    }

    @Override
    public void unsubscribe(Callback<T> callback) {
        synchronized (this) {
            callbacks.remove(callback);
        }
    }

    private class CallbackableRunnable implements Runnable {

        @Override
        public void run() {
            T result = null;
            Throwable error = null;
            try {
                result = task.call();
            } catch (Exception e) {
                error = e;
            } finally {
                if (error == null) {
                    resultSuccess(result);
                } else {
                    resultFailed(error);
                }
            }
        }
    }
}
