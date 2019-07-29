package com.globus_ltd.forecast_sber_test.data.repository;

import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;

import java.util.List;
import java.util.concurrent.Callable;

public interface LocalDataSource {
    Callable<List<Location>> getCities();

    Callable<Forecast> getForecast(String name);

    Callable<Forecast> getLatestForecast();

    Runnable insertForecast(Forecast forecast);
}
