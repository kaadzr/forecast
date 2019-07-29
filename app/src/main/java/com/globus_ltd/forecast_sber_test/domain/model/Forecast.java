package com.globus_ltd.forecast_sber_test.domain.model;

import java.util.Date;

public class Forecast {
    private final Location location;
    private final Weather weather;
    private final Date forecastDate;
    private final boolean cached;

    public Forecast(Location location, Weather weather, Date forecastDate, boolean cached) {
        this.location = location;
        this.weather = weather;
        this.forecastDate = forecastDate;
        this.cached = cached;
    }

    public Location getLocation() {
        return location;
    }

    public Weather getWeather() {
        return weather;
    }

    public Date getForecastDate() {
        return forecastDate;
    }

    public boolean isCached() {
        return cached;
    }
}
