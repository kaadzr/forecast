package com.globus_ltd.forecast_sber_test.data.source.remote;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

class ApiUtil {
    private static final String KEY_CITY_NAME = "q";
    private static final String KEY_APPID = "APPID";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_UNITS = "units";
    private static final String UNITS_METRIC = "metric";

    static URL createForecastUrl(@NonNull String cityName) throws MalformedURLException {
        Uri uri = prepareUriBuilder()
                .appendQueryParameter(KEY_CITY_NAME, cityName)
                .build();
        return new URL(uri.toString());
    }

    static URL createForecastUrl(float latitude, float longitude) throws MalformedURLException {
        Uri uri = prepareUriBuilder()
                .appendQueryParameter(KEY_LAT, Float.toString(latitude))
                .appendQueryParameter(KEY_LON, Float.toString(longitude))
                .build();
        return new URL(uri.toString());
    }

    private static Uri.Builder prepareUriBuilder() {
        return Uri.parse(BuildConfig.API_ENDPOINT).buildUpon()
                .appendQueryParameter(KEY_UNITS, UNITS_METRIC)
                .appendQueryParameter(KEY_APPID, BuildConfig.API_KEY);
    }
}
