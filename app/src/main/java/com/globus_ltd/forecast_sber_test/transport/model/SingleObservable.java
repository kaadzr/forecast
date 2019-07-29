package com.globus_ltd.forecast_sber_test.transport.model;

public interface SingleObservable<T> {
    void subscribe(Callback<T> callback);

    void unsubscribe(Callback<T> callback);
}
