package com.globus_ltd.forecast_sber_test;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Callable;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTest {
    protected  <T> Callable<T> simpleSuccessCallable(final T data) {
        return new Callable<T>() {
            @Override
            public T call() {
                return data;
            }
        };
    }

    protected  <T> Callable<T> simpleFailCallable(final Exception error) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                throw error;
            }
        };
    }
}
