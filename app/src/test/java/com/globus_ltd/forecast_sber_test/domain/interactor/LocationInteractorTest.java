package com.globus_ltd.forecast_sber_test.domain.interactor;

import android.location.Location;
import android.location.LocationManager;

import com.globus_ltd.forecast_sber_test.BaseTest;
import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.CurrentThreadAppExecutors;
import com.globus_ltd.forecast_sber_test.domain.model.Coordinates;
import com.globus_ltd.forecast_sber_test.domain.model.LowAccuracyException;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationInteractorTest extends BaseTest {
    private static final float ACCPETABLE_ACCURACY = 1f;
    private static final float INACCEPTABLE_ACCURACY = 1000f;
    private LocationInteractor interactor;
    @Mock
    LocationManager manager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Config.AppExecutors currentThreadExecutors = new CurrentThreadAppExecutors();
        interactor = new LocationInteractor(currentThreadExecutors);
    }

    @Test
    public void shouldReturnNetworkLocation() {
        Coordinates expected = new Coordinates(10, 10);
        Location location = mock(Location.class);
        when(location.getAccuracy()).thenReturn(ACCPETABLE_ACCURACY);
        when(location.getLatitude()).thenReturn(10.0);
        when(location.getLongitude()).thenReturn(10.0);
        when(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(location);
        Callback<Coordinates> callback = mock(Callback.class);

        Call<Coordinates> call = interactor.getLocation(manager);
        call.subscribe(callback);
        call.run();

        verify(callback, only()).onSuccess(expected);
    }

    @Test
    public void shouldReturnGpsLocation() {
        Coordinates expected = new Coordinates(10, 10);
        Location location = mock(Location.class);
        when(location.getAccuracy()).thenReturn(ACCPETABLE_ACCURACY);
        when(location.getLatitude()).thenReturn(10.0);
        when(location.getLongitude()).thenReturn(10.0);
        when(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        when(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(null);
        Callback<Coordinates> callback = mock(Callback.class);

        Call<Coordinates> call = interactor.getLocation(manager);
        call.subscribe(callback);
        call.run();

        verify(callback, only()).onSuccess(expected);
    }

    @Test
    public void shouldThrowException_becauseNoAcceptableLocations() {
        Location location = mock(Location.class);
        when(location.getAccuracy()).thenReturn(INACCEPTABLE_ACCURACY);
        when(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(location);
        when(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        Callback<Coordinates> callback = mock(Callback.class);

        Call<Coordinates> call = interactor.getLocation(manager);
        call.subscribe(callback);
        call.run();

        verify(callback, only()).onError(any(LowAccuracyException.class));
    }

    @Test
    public void shouldThrowException_becauseNoLocationsReceived() {
        when(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenReturn(null);
        when(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(null);
        Callback<Coordinates> callback = mock(Callback.class);

        Call<Coordinates> call = interactor.getLocation(manager);
        call.subscribe(callback);
        call.run();

        verify(callback, only()).onError(any(LowAccuracyException.class));
    }

    @Test
    public void shouldThrowSecurityException() {
        when(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)).thenThrow(new SecurityException());
        Callback<Coordinates> callback = mock(Callback.class);

        Call<Coordinates> call = interactor.getLocation(manager);
        call.subscribe(callback);
        call.run();

        verify(callback, only()).onError(any(SecurityException.class));
    }
}