package com.globus_ltd.forecast_sber_test.domain.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public abstract class ForecastRequest {
    public abstract boolean isReal();
    public static class ByCoordinates extends ForecastRequest {
        private final float latitude;
        private final float longitude;

        @Override
        public boolean isReal() {
            return true;
        }

        public ByCoordinates(float latitude, float longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public float getLatitude() {
            return latitude;
        }

        public float getLongitude() {
            return longitude;
        }
    }

    public static class ByName extends ForecastRequest {
        private final String name;

        public ByName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean isReal() {
            return true;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof ForecastRequest.ByName)){
                return false;
            }
            ByName other = (ByName) obj;
            return Objects.equals(name, other.getName());
        }
    }
    public static class Last extends ForecastRequest{
        @Override
        public boolean isReal() {
            return false;
        }
    }
}
