package com.globus_ltd.forecast_sber_test.transport.model;

public interface Callback<T> {
    void onSuccess(T result);

    void onError(Throwable error);
}
