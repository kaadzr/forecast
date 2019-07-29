package com.globus_ltd.forecast_sber_test.domain.model;

public class LowAccuracyException extends RuntimeException {
    public LowAccuracyException() {
        super("Location services provides too low accuracy");
    }
}
