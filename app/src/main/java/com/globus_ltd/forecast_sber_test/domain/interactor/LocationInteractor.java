package com.globus_ltd.forecast_sber_test.domain.interactor;

import android.location.Location;
import android.location.LocationManager;

import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.domain.model.Coordinates;
import com.globus_ltd.forecast_sber_test.domain.model.LowAccuracyException;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.SingleCall;

import java.util.concurrent.Callable;

public class LocationInteractor {
    private static final float LOCATION_DISTANCE = 100f;
    private final Config.AppExecutors executors;

    public LocationInteractor(Config.AppExecutors executors) {
        this.executors = executors;
    }

    public Call<Coordinates> getLocation(final LocationManager manager) {
        Callable<Coordinates> task = new Callable<Coordinates>() {
            @Override
            public Coordinates call() throws Exception {
                Location location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null || location.getAccuracy() >= LOCATION_DISTANCE) {
                    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (location != null && location.getAccuracy() <= LOCATION_DISTANCE) {
                    return new Coordinates((float) location.getLatitude(), (float) location.getLongitude());
                } else {
                    throw new LowAccuracyException();
                }
            }
        };
        return new SingleCall<>(task, executors.getIo());
    }
}
