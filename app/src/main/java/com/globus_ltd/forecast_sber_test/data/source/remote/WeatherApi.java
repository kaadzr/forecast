package com.globus_ltd.forecast_sber_test.data.source.remote;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.data.model.remote.ForecastResponse;
import com.globus_ltd.forecast_sber_test.data.repository.RemoteDataSource;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.transport.HttpClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

public class WeatherApi implements RemoteDataSource {
    private HttpClient client;
    private Gson gson;

    public WeatherApi(HttpClient client, Gson gson) {
        this.client = client;
        this.gson = gson;
    }

    @Override
    public Callable<Forecast> getForecast(@NonNull final String cityName) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                URL requestUrl = ApiUtil.createForecastUrl(cityName);
                return requestHttp(requestUrl);
            }
        };
    }

    @Override
    public Callable<Forecast> getForecast(final float latitude, final float longitude) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                URL requestUrl = ApiUtil.createForecastUrl(latitude, longitude);
                return requestHttp(requestUrl);
            }
        };
    }

    private Forecast requestHttp(URL requestUrl) throws IOException {
        String result = client.executeGetRequest(requestUrl);
        ForecastResponse response = gson.fromJson(result, ForecastResponse.class);
        return ForecastResponseMappers.responseToForecast(response);
    }
}
