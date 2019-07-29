package com.globus_ltd.forecast_sber_test.presentation.presentation.forecast;

import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.domain.interactor.ForecastInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationsInteractor;
import com.globus_ltd.forecast_sber_test.domain.model.Coordinates;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;
import com.globus_ltd.forecast_sber_test.presentation.presentation.util.CallbackHandler;
import com.globus_ltd.forecast_sber_test.transport.model.Call;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ForecastPresenter {
    private WeakReference<ForecastView> view = null;
    private ForecastViewState currentState;
    private boolean isAttached = false;
    private final ForecastInteractor forecastInteractor;
    private final LocationsInteractor locationsListInteractor;
    private final LocationInteractor currentLocationInteractor;
    private final Config.AppExecutors executors;
    private Call<Forecast> currentForecastJob;
    private Call<List<Location>> getSavedLocationsJob;
    private Call<Coordinates> getCoordinatesJob;
    private LocationManager locationManager;

    private CallbackHandler<Forecast> forecastHandler;
    private CallbackHandler<List<Location>> savedLocationsHandler;
    private CallbackHandler<Coordinates> locationHandler;

    public ForecastPresenter(ForecastInteractor forecastInteractor,
                             LocationsInteractor locationsListInteractor,
                             LocationInteractor currentLocationInteractor,
                             Config.AppExecutors executors, ForecastViewState initialState) {
        this.forecastInteractor = forecastInteractor;
        this.locationsListInteractor = locationsListInteractor;
        this.currentLocationInteractor = currentLocationInteractor;
        this.currentState = initialState;
        this.executors = executors;
        initHandlers();
    }

    private void initHandlers() {
        savedLocationsHandler = new CallbackHandler<List<Location>>(executors.getMain()) {
            @Override
            public void response(List<Location> data) {
                locationsListReceived(data);
            }

            @Override
            public void failure(Throwable error) {
                //does nothing
            }
        };
        forecastHandler = new CallbackHandler<Forecast>(executors.getMain()) {
            @Override
            public void response(Forecast data) {
                forecastReceived(data);
            }

            @Override
            public void failure(Throwable error) {
                forecastRequestFailed();
            }

        };
        locationHandler = new CallbackHandler<Coordinates>(executors.getMain()) {
            @Override
            public void response(Coordinates data) {
                locationReceived(data);
            }

            @Override
            public void failure(Throwable error) {
                locationRequestFailed(error);
            }
        };
    }

    public void attachView(ForecastView view) {
        this.view = new WeakReference<>(view);
        isAttached = true;
        if (locationManager == null) {
            locationManager = view.getLocationManager();
            view.requestLocationPermissions();
        }
        if (currentState.getSavedLocations() == null) {
            requestSavedLocations();
        }
        view.populateViewState(currentState);
    }

    public void detachView() {
        this.view = null;
        isAttached = false;
    }

    public void locationPermissionGranted() {
        requestLocation();
    }

    public void locationPermissionForbidden() {
        cancelCurrentLocationJob();
        onNewState(ForecastStateReducer.onLocationForbidden(currentState));
        requestForecast(new ForecastRequest.Last());
    }

    public void requestLocation() {
        cancelCurrentLocationJob();
        getCoordinatesJob = currentLocationInteractor.getLocation(locationManager);
        getCoordinatesJob.subscribe(locationHandler.getCallback());
        onNewState(ForecastStateReducer.onLocationRequestStarted(currentState));
        getCoordinatesJob.run();
    }

    private void cancelCurrentLocationJob() {
        if (getCoordinatesJob != null) {
            getCoordinatesJob.unsubscribe(locationHandler.getCallback());
            getCoordinatesJob.cancel();
        }
    }

    private void locationReceived(Coordinates coordinates) {
        requestForecast(new ForecastRequest.ByCoordinates(
                coordinates.getLatitude(),
                coordinates.getLongitude()));
    }

    private void locationRequestFailed(Throwable error) {
        onNewState(ForecastStateReducer.onLocationRequestFailed(currentState));
        if (error instanceof SecurityException) {
            locationPermissionForbidden();
        } else {
            requestForecast(new ForecastRequest.Last());
        }
    }

    public void retry(@NonNull ForecastRequest request) {
        requestForecast(request);
    }

    public void locationNameReceived(@NonNull String name) {
        requestForecast(new ForecastRequest.ByName(name));
    }

    private void requestSavedLocations() {
        cancelSavedLocationsJob();
        getSavedLocationsJob = locationsListInteractor.getLocations();
        getSavedLocationsJob.subscribe(savedLocationsHandler.getCallback());
        getSavedLocationsJob.run();
    }

    private void cancelSavedLocationsJob() {
        if (getSavedLocationsJob != null) {
            getSavedLocationsJob.unsubscribe(savedLocationsHandler.getCallback());
            getSavedLocationsJob.cancel();
        }
    }

    private void locationsListReceived(List<Location> locations) {
        List<String> locationNames = new ArrayList<>();
        for (Location location : locations) {
            locationNames.add(location.getLocationName());
        }
        onNewState(ForecastStateReducer.onSavedLocationsReceived(currentState, locationNames));
    }

    private void requestForecast(ForecastRequest request) {
        cancelCurrentJob();
        currentForecastJob = forecastInteractor.getForecast(request);
        currentForecastJob.subscribe(forecastHandler.getCallback());
        onNewState(ForecastStateReducer.onJobStarted(currentState, request));
        currentForecastJob.run();

    }

    private void cancelCurrentJob() {
        if (currentForecastJob != null) {
            currentForecastJob.unsubscribe(forecastHandler.getCallback());
            currentForecastJob.cancel();
        }
    }

    private void forecastReceived(Forecast forecast) {
        requestSavedLocations();
        onNewState(ForecastStateReducer.onForecastReceived(currentState, forecast));
    }

    private void forecastRequestFailed() {
        onNewState(ForecastStateReducer.onForecastRequestFailed(currentState));
    }

    public void requestCurrentState() {
        onNewState(currentState);
    }

    private void onNewState(final ForecastViewState state) {
        currentState = state;
        sendNewState(state);
    }

    private void sendNewState(final ForecastViewState state) {
        if (isAttached && view != null) {
            ForecastView forecastView = view.get();
            if (forecastView != null) {
                forecastView.populateViewState(state);
            }
        }
    }

    public void onDestroy() {
        cancelCurrentJob();
        cancelSavedLocationsJob();
        cancelCurrentLocationJob();
        locationManager = null;
    }
}
