package com.globus_ltd.forecast_sber_test.data.source.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.globus_ltd.forecast_sber_test.data.model.CacheException;
import com.globus_ltd.forecast_sber_test.data.model.local.CityEntity;
import com.globus_ltd.forecast_sber_test.data.model.local.ForecastEntity;
import com.globus_ltd.forecast_sber_test.data.repository.LocalDataSource;
import com.globus_ltd.forecast_sber_test.data.source.local.mappers.CityEntityMapper;
import com.globus_ltd.forecast_sber_test.data.source.local.mappers.ForecastEntityMapper;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ForecastDao implements LocalDataSource {
    private DbHelper dbHelper;

    public ForecastDao(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public Runnable insertForecast(final Forecast forecast) {
        return new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ForecastEntity entity = ForecastEntityMapper.forecastToForecastEntity(forecast);
                ContentValues values = createForecastInsertionValues(entity);

                db.insertWithOnConflict(DbSchema.Tables.FORECAST, null,
                        values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        };
    }

    @Override
    public Callable<List<Location>> getCities() {
        return new Callable<List<Location>>() {
            @Override
            public List<Location> call() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String[] projection = {
                        DbSchema.ForecastTable.CITY_ID,
                        DbSchema.ForecastTable.CITY_NAME
                };
                List<Location> locations = new ArrayList<>();
                try (Cursor cursor = db.query(
                        DbSchema.Tables.FORECAST,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null
                )) {
                    CityEntity city;
                    Location location;
                    while (cursor.moveToNext()) {
                        city = parseCity(cursor);
                        location = CityEntityMapper.cityEntityToLocation(city);
                        locations.add(location);
                    }
                }
                return locations;
            }
        };
    }

    @Override
    public Callable<Forecast> getForecast(final String name) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws CacheException {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String[] projection = DbSchema.ForecastTable.COLUMNS;
                String selection = DbSchema.ForecastTable.CITY_NAME + " = ?";
                String[] arguments = {name};
                ForecastEntity forecast = null;
                try (Cursor cursor = db.query(
                        DbSchema.Tables.FORECAST,
                        projection,
                        selection,
                        arguments,
                        null,
                        null,
                        null
                )) {
                    if (cursor.moveToFirst()) {
                        forecast = parseCursor(cursor);
                    }
                } catch (Exception e) {
                    throw new CacheException("Couldn't get forecast for location " + name, e);
                }
                if (forecast == null) {
                    throw new CacheException("There is no saved forecast for location " + name);
                }
                return ForecastEntityMapper.forecastEntityToForecast(forecast);
            }
        };
    }

    @Override
    public Callable<Forecast> getLatestForecast() {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws CacheException {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ForecastEntity forecast = null;
                try (Cursor cursor = db.rawQuery(GET_LATEST_FORECAST_QUERY, null)) {
                    if (cursor.moveToFirst()) {
                        forecast = parseCursor(cursor);
                    }
                } catch (Exception e) {
                    throw new CacheException("Couldn't get last forecast", e);
                }
                if (forecast == null) {
                    throw new CacheException("There is no saved forecasts");
                }
                return ForecastEntityMapper.forecastEntityToForecast(forecast);
            }
        };
    }

    private ForecastEntity parseCursor(Cursor cursor) {
        return new ForecastEntity(
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.CITY_ID)),
                cursor.getString(cursor.getColumnIndex(DbSchema.ForecastTable.CITY_NAME)),
                cursor.getString(cursor.getColumnIndex(DbSchema.ForecastTable.WEATHER_ICON)),
                cursor.getString(cursor.getColumnIndex(DbSchema.ForecastTable.WEATHER_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.MIN_TEMPERATURE)),
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.MAX_TEMPERATURE)),
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.TEMPERATURE)),
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.PRESSURE)),
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.HUMIDITY)),
                cursor.getLong(cursor.getColumnIndex(DbSchema.ForecastTable.TIMESTAMP))
        );
    }

    private CityEntity parseCity(Cursor cursor) {
        return new CityEntity(
                cursor.getInt(cursor.getColumnIndex(DbSchema.ForecastTable.CITY_ID)),
                cursor.getString(cursor.getColumnIndex(DbSchema.ForecastTable.CITY_NAME))
        );
    }

    private ContentValues createForecastInsertionValues(ForecastEntity entity) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.ForecastTable.CITY_ID, entity.getCityId());
        values.put(DbSchema.ForecastTable.CITY_NAME, entity.getCityName());
        values.put(DbSchema.ForecastTable.WEATHER_ICON, entity.getWeatherIcon());
        values.put(DbSchema.ForecastTable.WEATHER_DESCRIPTION, entity.getWeatherDescription());
        values.put(DbSchema.ForecastTable.MIN_TEMPERATURE, entity.getMinTemperature());
        values.put(DbSchema.ForecastTable.MAX_TEMPERATURE, entity.getMaxTemperature());
        values.put(DbSchema.ForecastTable.TEMPERATURE, entity.getTemperature());
        values.put(DbSchema.ForecastTable.PRESSURE, entity.getPressure());
        values.put(DbSchema.ForecastTable.HUMIDITY, entity.getHumidity());
        values.put(DbSchema.ForecastTable.TIMESTAMP, entity.getTimestamp());
        return values;
    }

    public void close() {
        dbHelper.getWritableDatabase().close();
    }

    private static final String GET_LATEST_FORECAST_QUERY =
            "SELECT * FROM " + DbSchema.Tables.FORECAST + " ORDER BY "
                    + DbSchema.ForecastTable.TIMESTAMP + " DESC LIMIT 1";
}
