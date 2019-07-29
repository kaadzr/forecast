package com.globus_ltd.forecast_sber_test.presentation.presentation.util;

import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import java.util.concurrent.Executor;

public abstract class CallbackHandler<T> {
    private Callback<T> callback;

    public CallbackHandler(final Executor mainExecutor) {
        callback = new Callback<T>() {
            @Override
            public void onSuccess(final T result) {
                mainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        response(result);
                    }
                });
            }

            @Override
            public void onError(final Throwable error) {
                mainExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        failure(error);
                    }
                });
            }
        };
    }

    public Callback<T> getCallback() {
        return callback;
    }

    protected abstract void response(T data);

    protected abstract void failure(Throwable error);

}
