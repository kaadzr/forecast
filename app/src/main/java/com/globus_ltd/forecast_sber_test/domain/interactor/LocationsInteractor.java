package com.globus_ltd.forecast_sber_test.domain.interactor;

import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.SingleCall;

import java.util.List;

public class LocationsInteractor {
    private WeatherRepository repository;
    private Config.AppExecutors executors;

    public LocationsInteractor(WeatherRepository repository, Config.AppExecutors executors) {
        this.repository = repository;
        this.executors = executors;
    }

    public Call<List<Location>> getLocations() {
        return new SingleCall<>(
                repository.getLocations(),
                executors.getIo()
        );
    }
}
