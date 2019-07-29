package com.globus_ltd.forecast_sber_test.domain.interactor;

import com.globus_ltd.forecast_sber_test.BaseTest;
import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.CurrentThreadAppExecutors;
import com.globus_ltd.forecast_sber_test.data.model.CacheException;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocationsInteractorTest extends BaseTest {
    private LocationsInteractor interactor;
    @Mock
    WeatherRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Config.AppExecutors currentThreadExecutors = new CurrentThreadAppExecutors();
        interactor = new LocationsInteractor(
                repository,
                currentThreadExecutors
        );
    }

    @Test
    public void shouldReturnLocationsList() {
        List<Location> expected = Arrays.asList(
                new Location(1, "first"),
                new Location(2, "second")
        );
        Callback<List<Location>> callback = (Callback<List<Location>>) mock(Callback.class);
        when(repository.getLocations()).thenReturn(simpleSuccessCallable(expected));

        Call<List<Location>> call = interactor.getLocations();
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldThrowCacheException() {
        Callback<List<Location>> callback = (Callback<List<Location>>) mock(Callback.class);
        when(repository.getLocations()).thenReturn(this.<List<Location>>simpleFailCallable(new CacheException("")));

        Call<List<Location>> call = interactor.getLocations();
        call.subscribe(callback);
        call.run();

        verify(callback).onError(any(CacheException.class));
    }
}