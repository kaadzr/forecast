package com.globus_ltd.forecast_sber_test.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.globus_ltd.forecast_sber_test.data.repository.LocalDataSource;
import com.globus_ltd.forecast_sber_test.data.repository.RemoteDataSource;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;
import com.globus_ltd.forecast_sber_test.widget.WidgetUpdateUtil;

import java.util.List;
import java.util.concurrent.Callable;

public class WeatherRepositoryImpl implements WeatherRepository {
    private final RemoteDataSource remoteDataSource;
    private final LocalDataSource localDataSource;
    private final WidgetUpdateUtil widgetUpdateUtil;

    public WeatherRepositoryImpl(RemoteDataSource remoteDataSource,
                                 LocalDataSource localDataSource,
                                 WidgetUpdateUtil widgetUpdateUtil) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.widgetUpdateUtil = widgetUpdateUtil;
    }

    @Override
    public Callable<Forecast> getForecast(@NonNull final String cityName) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                Forecast result = remoteDataSource.getForecast(cityName).call();
                cache(result);
                return result;
            }
        };
    }

    @Override
    public Callable<Forecast> getForecast(final float latitude, final float longitude) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                Forecast result = remoteDataSource.getForecast(latitude, longitude).call();
                cache(result);
                return result;
            }
        };
    }

    @WorkerThread
    private void cache(@Nullable Forecast forecast) {
        if (forecast != null) {
            localDataSource.insertForecast(forecast).run();
            widgetUpdateUtil.updateWidgets();
        }
    }

    @Override
    public Callable<Forecast> getCachedForecast(final String cityName) {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                return localDataSource.getForecast(cityName).call();
            }
        };
    }

    @Override
    public Callable<Forecast> getLastForecast() {
        return new Callable<Forecast>() {
            @Override
            public Forecast call() throws Exception {
                return localDataSource.getLatestForecast().call();
            }
        };
    }

    @Override
    public Callable<List<Location>> getLocations() {
        return new Callable<List<Location>>() {
            @Override
            public List<Location> call() throws Exception {
                return localDataSource.getCities().call();
            }
        };
    }
}
