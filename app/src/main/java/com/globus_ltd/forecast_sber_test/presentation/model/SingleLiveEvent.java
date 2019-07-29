package com.globus_ltd.forecast_sber_test.presentation.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SingleLiveEvent<T> {
    @Nullable
    private T data;

    public SingleLiveEvent(@Nullable T data) {
        this.data = data;
    }

    /**
     * Wipes data after call
     * @return data
     */
    @Nullable
    public T getAndErase() {
        T result = data;
        data = null;
        return result;
    }

    @Nullable
    private T getData() {
        return data;
    }

    public boolean isExists() {
        return data != null;
    }

    public void setData(@NonNull T data) {
        this.data = data;
    }
}
