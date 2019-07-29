package com.globus_ltd.forecast_sber_test.transport;

import java.io.IOException;

public class HttpFailedException extends IOException {
    private int code;

    public HttpFailedException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
