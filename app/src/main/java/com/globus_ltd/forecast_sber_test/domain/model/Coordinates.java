package com.globus_ltd.forecast_sber_test.domain.model;

import androidx.annotation.Nullable;

public class Coordinates {
    private final float latitude;
    private final float longitude;

    public Coordinates(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Coordinates)) {
            return false;
        }
        Coordinates coordinates = (Coordinates) obj;
        return latitude == coordinates.latitude
                && longitude == coordinates.longitude;
    }
}
