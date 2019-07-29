package com.globus_ltd.forecast_sber_test.data.source.local.mappers;

import com.globus_ltd.forecast_sber_test.data.model.local.ForecastEntity;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.Weather;
import com.globus_ltd.forecast_sber_test.domain.model.WeatherIcon;

import java.util.Date;

public class ForecastEntityMapper {
    public static ForecastEntity forecastToForecastEntity(Forecast forecast) {
        return new ForecastEntity(
                forecast.getLocation().getLocationId(),
                forecast.getLocation().getLocationName(),
                forecast.getWeather().getWeatherIcon().name(),
                forecast.getWeather().getWeatherDescription(),
                Math.round(forecast.getWeather().getMinTemperature() * 100f),
                Math.round(forecast.getWeather().getMaxTemperature() * 100f),
                Math.round(forecast.getWeather().getTemperature() * 100f),
                forecast.getWeather().getPressure(),
                forecast.getWeather().getHumidity(),
                forecast.getForecastDate().getTime()
        );
    }

    public static Forecast forecastEntityToForecast(ForecastEntity entity) {
        return new Forecast(
                new Location(
                        entity.getCityId(),
                        entity.getCityName()
                ),
                new Weather(
                        WeatherIcon.valueOf(entity.getWeatherIcon()),
                        entity.getWeatherDescription(),
                        entity.getMinTemperature() / 100.f,
                        entity.getMaxTemperature() / 100.f,
                        entity.getTemperature() / 100.f,
                        entity.getPressure(),
                        entity.getHumidity()
                ),
                new Date(entity.getTimestamp()),
                true
        );
    }
}
