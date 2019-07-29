package com.globus_ltd.forecast_sber_test.data.model.local;

public class ForecastEntity {
    private int cityId;
    private String cityName;
    private String weatherIcon;
    private String weatherDescription;
    private int minTemperature;
    private int maxTemperature;
    private int temperature;
    private int pressure;
    private int humidity;
    private long timestamp;

    public ForecastEntity(int cityId, String cityName, String weatherIcon,
                          String weatherDescription, int minTemperature, int maxTemperature,
                          int temperature, int pressure, int humidity, long timestamp) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.weatherIcon = weatherIcon;
        this.weatherDescription = weatherDescription;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
