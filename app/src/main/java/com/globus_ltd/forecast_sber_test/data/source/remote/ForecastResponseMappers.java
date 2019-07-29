package com.globus_ltd.forecast_sber_test.data.source.remote;

import com.globus_ltd.forecast_sber_test.data.model.remote.ForecastResponse;
import com.globus_ltd.forecast_sber_test.data.model.remote.MainDataResponse;
import com.globus_ltd.forecast_sber_test.data.model.remote.WeatherResponse;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.Weather;
import com.globus_ltd.forecast_sber_test.domain.model.WeatherIcon;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ForecastResponseMappers {
    static Forecast responseToForecast(ForecastResponse response) {
        return new Forecast(
                new Location(
                        response.getId(),
                        response.getName()
                ),
                responseToWeather(response.getWeather(), response.getMain()),
                new Date(TimeUnit.SECONDS.toMillis(response.getDt())),
                false);
    }

    private static Weather responseToWeather(List<WeatherResponse> weatherResponses,
                                             MainDataResponse mainDataResponse) {
        WeatherResponse weatherResponse = weatherResponses.get(0);
        return new Weather(
                WeatherIcon.of(weatherResponse.getIcon()),
                weatherResponse.getDescription(),
                roundTwoSymbols(mainDataResponse.getTempMin()),
                roundTwoSymbols(mainDataResponse.getTempMax()),
                roundTwoSymbols(mainDataResponse.getTemp()),
                (int) Math.round(mainDataResponse.getPressure()),
                mainDataResponse.getHumidity()
        );
    }

    private static float roundTwoSymbols(double value) {
        return Math.round(value * 100.0) / 100.0f;
    }
}
