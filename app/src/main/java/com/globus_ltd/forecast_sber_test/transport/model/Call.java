package com.globus_ltd.forecast_sber_test.transport.model;

public interface Call<T> extends SingleObservable<T> {
    /**
     * You should call this method only once per instance
     * Following calls won't have any effect
     */
    void run();

    void cancel();
}
