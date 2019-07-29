package com.globus_ltd.forecast_sber_test.domain.interactor;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.SingleCall;

import java.util.concurrent.Callable;

public class ForecastInteractor {
    private WeatherRepository repository;
    private Config.AppExecutors executors;

    public ForecastInteractor(WeatherRepository repository, Config.AppExecutors executors) {
        this.repository = repository;
        this.executors = executors;
    }

    public Call<Forecast> getForecast(ForecastRequest request) {
        if (request instanceof ForecastRequest.ByCoordinates) {
            ForecastRequest.ByCoordinates unwrappedRequest = (ForecastRequest.ByCoordinates) request;
            return getForecast(
                    unwrappedRequest.getLatitude(),
                    unwrappedRequest.getLongitude());
        } else if (request instanceof ForecastRequest.ByName) {
            ForecastRequest.ByName unwrappedRequest = (ForecastRequest.ByName) request;
            return getForecast(unwrappedRequest.getName());
        } else if (request instanceof ForecastRequest.Last) {
            return getForecast();
        } else {
            throw new IllegalArgumentException("Unknown request type");
        }
    }

    private Call<Forecast> getForecast(@NonNull final String cityName) {
        Callable<Forecast> task = new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                Forecast result;
                try {
                    result = repository.getForecast(cityName).call();
                } catch (Exception e) {
                    result = repository.getCachedForecast(cityName).call();
                }
                return result;
            }
        };
        return new SingleCall<>(task, executors.getIo());
    }

    private Call<Forecast> getForecast(final float latitude, final float longitude) {
        Callable<Forecast> task = new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                Forecast result;
                try {
                    result = repository.getForecast(latitude, longitude).call();
                } catch (Exception e) {
                    result = repository.getLastForecast().call();
                }
                return result;
            }
        };
        return new SingleCall<>(task, executors.getIo());
    }

    private Call<Forecast> getForecast() {
        return new SingleCall<>(repository.getLastForecast(), executors.getIo());
    }
}
