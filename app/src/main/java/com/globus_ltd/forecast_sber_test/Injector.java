package com.globus_ltd.forecast_sber_test;

import com.globus_ltd.forecast_sber_test.data.WeatherRepositoryImpl;
import com.globus_ltd.forecast_sber_test.data.repository.LocalDataSource;
import com.globus_ltd.forecast_sber_test.data.repository.RemoteDataSource;
import com.globus_ltd.forecast_sber_test.data.source.local.ForecastDao;
import com.globus_ltd.forecast_sber_test.data.source.remote.WeatherApi;
import com.globus_ltd.forecast_sber_test.domain.interactor.ForecastInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationsInteractor;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;

public class Injector {
    public final Config.AppExecutors executors;
    public final ForecastInteractor forecastInteractor;
    public final LocationsInteractor locationsListInteractor;
    public final LocationInteractor locationInteractor;
    public final LocalDataSource localDataSource;

    Injector(Config config) {
        RemoteDataSource remoteDataSource = new WeatherApi(config.client, config.gson);
        localDataSource = new ForecastDao(config.dbHelper);
        WeatherRepository repository = new WeatherRepositoryImpl(
                remoteDataSource, localDataSource, config.widgetUpdateUtil);
        executors = config.executors;
        forecastInteractor = new ForecastInteractor(repository, executors);
        locationsListInteractor = new LocationsInteractor(repository, executors);
        locationInteractor = new LocationInteractor(executors);
    }
}
