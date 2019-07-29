package com.globus_ltd.forecast_sber_test;

import android.app.Application;

import androidx.annotation.VisibleForTesting;

public class ForecastApp extends Application {
    private Injector injector;

    @Override
    public void onCreate() {
        super.onCreate();
        Config config = new Config(this);
        injector = new Injector(config);
    }

    public Injector getInjector() {
        return injector;
    }

    @VisibleForTesting
    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}
