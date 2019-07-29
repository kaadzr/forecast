package com.globus_ltd.forecast_sber_test.domain.repository;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;

import java.util.List;
import java.util.concurrent.Callable;

public interface WeatherRepository {
    Callable<Forecast> getForecast(@NonNull String cityName);

    Callable<Forecast> getForecast(float latitude, float longitude);

    Callable<Forecast> getCachedForecast(String cityName);

    Callable<Forecast> getLastForecast();

    Callable<List<Location>> getLocations();

}
