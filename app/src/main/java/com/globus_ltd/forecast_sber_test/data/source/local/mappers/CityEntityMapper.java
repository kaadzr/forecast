package com.globus_ltd.forecast_sber_test.data.source.local.mappers;

import com.globus_ltd.forecast_sber_test.data.model.local.CityEntity;
import com.globus_ltd.forecast_sber_test.domain.model.Location;

public class CityEntityMapper {
    public static Location cityEntityToLocation(CityEntity entity) {
        return new Location(
                entity.getId(),
                entity.getName()
        );
    }
}
