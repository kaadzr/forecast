package com.globus_ltd.forecast_sber_test.presentation.presentation.forecast;

import android.location.LocationManager;

import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;

public interface ForecastView {
    void populateViewState(ForecastViewState state);

    LocationManager getLocationManager();

    void requestLocationPermissions();
}
