package com.globus_ltd.forecast_sber_test.data.model.local;

public class CityEntity {
    private final int id;
    private final String name;

    public CityEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
