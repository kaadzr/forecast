package com.globus_ltd.forecast_sber_test;

import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import java.util.HashSet;
import java.util.Set;

public class BlockingCall<T> implements Call<T> {
    private T data;
    private Throwable error;
    private boolean executed = false;
    private boolean canceled = false;

    public BlockingCall(T data) {
        this.data = data;
    }

    private Set<Callback<T>> callbacks = new HashSet<>();

    public BlockingCall(Throwable error) {
        this.error = error;
    }

    @Override
    public void run() {
        if (executed || canceled) {
            return;
        }
        for (Callback<T> callback : callbacks) {
            if (data != null) {
                callback.onSuccess(data);
            } else {
                callback.onError(error);
            }
        }
    }

    @Override
    public void cancel() {
        //does nothing
    }

    @Override
    public void subscribe(Callback<T> callback) {
        callbacks.add(callback);
    }

    @Override
    public void unsubscribe(Callback<T> callback) {
        callbacks.remove(callback);
    }
}
