package com.globus_ltd.forecast_sber_test.domain.model;

public class Location {
    private final int locationId;
    private final String locationName;

    public Location(int locationId, String locationName) {
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public int getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }
}
