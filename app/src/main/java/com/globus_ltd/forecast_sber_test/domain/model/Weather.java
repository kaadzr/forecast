package com.globus_ltd.forecast_sber_test.domain.model;

public class Weather {
    private final WeatherIcon weatherIcon;
    private final String weatherDescription;
    private final float minTemperature;
    private final float maxTemperature;
    private final float temperature;
    private final int pressure;
    private final int humidity;

    public Weather(WeatherIcon weatherIcon, String weatherDescription, float minTemperature,
                   float maxTemperature, float temperature, int pressure, int humidity) {
        this.weatherIcon = weatherIcon;
        this.weatherDescription = weatherDescription;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public WeatherIcon getWeatherIcon() {
        return weatherIcon;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }
}
