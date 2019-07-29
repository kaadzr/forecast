package com.globus_ltd.forecast_sber_test.data.repository;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.domain.model.Forecast;

import java.util.concurrent.Callable;

public interface RemoteDataSource {

    Callable<Forecast> getForecast(@NonNull String cityName);

    Callable<Forecast> getForecast(float latitude, float longitude);
}
