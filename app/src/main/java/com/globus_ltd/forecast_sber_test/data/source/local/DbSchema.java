package com.globus_ltd.forecast_sber_test.data.source.local;

interface DbSchema {
    String CREATE_FORECAST = "CREATE TABLE " + Tables.FORECAST + "("
            + ForecastTable.CITY_ID + " INTEGER PRIMARY KEY, "
            + ForecastTable.CITY_NAME + " TEXT,"
            + ForecastTable.WEATHER_ICON + " TEXT, "
            + ForecastTable.WEATHER_DESCRIPTION + " TEXT, "
            + ForecastTable.MIN_TEMPERATURE + " INTEGER, "
            + ForecastTable.MAX_TEMPERATURE + " INTEGER, "
            + ForecastTable.TEMPERATURE + " INTEGER, "
            + ForecastTable.PRESSURE + " INTEGER, "
            + ForecastTable.HUMIDITY + " INTEGER, "
            + ForecastTable.TIMESTAMP + " INTEGER"
            + ")";

    interface Tables {
        String FORECAST = "forecast";
    }

    interface ForecastTable {
        String CITY_ID = "city_id";
        String CITY_NAME = "city_name";
        String WEATHER_ICON = "weather_icon";
        String WEATHER_DESCRIPTION = "weather_description";
        String MIN_TEMPERATURE = "min_temperature";
        String MAX_TEMPERATURE = "max_temperature";
        String TEMPERATURE = "temperature";
        String PRESSURE = "pressure";
        String HUMIDITY = "humidity";
        String TIMESTAMP = "timestamp";
        String[] COLUMNS = {CITY_ID, CITY_NAME, WEATHER_ICON,
                WEATHER_DESCRIPTION, MIN_TEMPERATURE, MAX_TEMPERATURE,
                TEMPERATURE, PRESSURE, HUMIDITY, TIMESTAMP};
    }
}
