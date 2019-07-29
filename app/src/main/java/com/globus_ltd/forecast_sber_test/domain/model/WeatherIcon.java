package com.globus_ltd.forecast_sber_test.domain.model;

import androidx.annotation.DrawableRes;

import com.globus_ltd.forecast_sber_test.R;

public enum WeatherIcon {
    CLEAR_SKY(R.drawable.ic_weather_sunny, "01"),
    FEW_CLOUDS(R.drawable.ic_weather_partlycloudy, "02"),
    SCATTERED_CLOUDS(R.drawable.ic_weather_cloudy, "03"),
    BROKEN_CLOUDS(R.drawable.ic_weather_cloudy, "04"),
    SHOWER_RAIN(R.drawable.ic_weather_rainy, "09"),
    RAIN(R.drawable.ic_weather_rainy, "10"),
    THUNDERSTORM(R.drawable.ic_weather_lightning, "11"),
    SNOW(R.drawable.ic_weather_snowy, "13"),
    MIST(R.drawable.ic_weather_fog, "50");
    @DrawableRes
    private int iconRes;
    private String code;

    WeatherIcon(int iconRes, String code) {
        this.iconRes = iconRes;
        this.code = code;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getCode() {
        return code;
    }

    public static WeatherIcon of(String code) {
        for (WeatherIcon weather : values()) {
            if (code.startsWith(weather.code)) {
                return weather;
            }
        }
        throw new IllegalArgumentException("Unknown icon");
    }
}
