package com.globus_ltd.forecast_sber_test.data;

import com.globus_ltd.forecast_sber_test.BaseTest;
import com.globus_ltd.forecast_sber_test.data.model.CacheException;
import com.globus_ltd.forecast_sber_test.data.repository.LocalDataSource;
import com.globus_ltd.forecast_sber_test.data.repository.RemoteDataSource;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.transport.HttpFailedException;
import com.globus_ltd.forecast_sber_test.widget.WidgetUpdateUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WeatherRepositoryImplTest extends BaseTest {
    private WeatherRepositoryImpl weatherRepository;
    @Mock
    LocalDataSource localDataSource;
    @Mock
    RemoteDataSource remoteDataSource;
    @Mock
    WidgetUpdateUtil widgetUpdateUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        weatherRepository = new WeatherRepositoryImpl(remoteDataSource, localDataSource, widgetUpdateUtil);
    }

    @Test
    public void shouldReturnProperForecastByLocationNameAndCacheIt() throws Exception {
        String expectedName = "expectedName";
        Forecast expectedForecast = Mockito.mock(Forecast.class);
        Runnable saveInDbRunnable = mock(Runnable.class);
        when(remoteDataSource.getForecast(expectedName))
                .thenReturn(simpleSuccessCallable(expectedForecast));
        when(localDataSource.insertForecast(expectedForecast)).thenReturn(saveInDbRunnable);

        Callable<Forecast> forecastCallable = weatherRepository.getForecast(expectedName);
        Forecast actual = forecastCallable.call();

        verify(saveInDbRunnable).run();
        assertEquals(expectedForecast, actual);
    }

    @Test(expected = HttpFailedException.class)
    public void shouldThrowHttpExceptionGettingForecastByLocationName() throws Exception {
        String expectedName = "expectedName";
        int code = 404;
        when(remoteDataSource.getForecast(expectedName))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(code)));

        Callable<Forecast> forecastCallable = weatherRepository.getForecast(expectedName);
        forecastCallable.call();
    }

    @Test
    public void shouldReturnProperForecastByCoordinatesAndCacheIt() throws Exception {
        int latitude = 10;
        int longitude = 10;
        Forecast expectedForecast = Mockito.mock(Forecast.class);
        Runnable saveInDbRunnable = mock(Runnable.class);
        when(remoteDataSource.getForecast(latitude, longitude))
                .thenReturn(simpleSuccessCallable(expectedForecast));
        when(localDataSource.insertForecast(expectedForecast)).thenReturn(saveInDbRunnable);

        Callable<Forecast> forecastCallable = weatherRepository.getForecast(latitude, longitude);
        Forecast actual = forecastCallable.call();

        verify(saveInDbRunnable).run();
        assertEquals(expectedForecast, actual);
    }

    @Test(expected = HttpFailedException.class)
    public void shouldThrowHttpExceptionGettingForecastByCoordinates() throws Exception {
        int latitude = 10;
        int longitude = 10;
        int code = 404;
        when(remoteDataSource.getForecast(latitude, longitude))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(code)));

        Callable<Forecast> forecastCallable = weatherRepository.getForecast(latitude, longitude);
        forecastCallable.call();
    }

    @Test
    public void shouldReturnProperCachedForecast() throws Exception {
        String expectedName = "expectedName";
        Forecast expectedForecast = mock(Forecast.class);
        when(localDataSource.getForecast(expectedName))
                .thenReturn(simpleSuccessCallable(expectedForecast));

        Callable<Forecast> forecastCallable = weatherRepository.getCachedForecast(expectedName);
        Forecast actual = forecastCallable.call();

        assertEquals(expectedForecast, actual);
    }

    @Test(expected = CacheException.class)
    public void shouldThrowCacheExceptionGettingCachedForecast() throws Exception {
        String expectedName = "expectedName";
        when(localDataSource.getForecast(expectedName))
                .thenReturn(this.<Forecast>simpleFailCallable(new CacheException("")));

        Callable<Forecast> forecastCallable = weatherRepository.getCachedForecast(expectedName);
        forecastCallable.call();
    }

    @Test
    public void shouldReturnProperLastForecast() throws Exception {
        Forecast expectedForecast = mock(Forecast.class);
        when(localDataSource.getLatestForecast())
                .thenReturn(simpleSuccessCallable(expectedForecast));

        Callable<Forecast> forecastCallable = weatherRepository.getLastForecast();
        Forecast actual = forecastCallable.call();

        assertEquals(expectedForecast, actual);
    }

    @Test(expected = CacheException.class)
    public void shouldThrowCacheExceptionGettingLastForecast() throws Exception {
        when(localDataSource.getLatestForecast())
                .thenReturn(this.<Forecast>simpleFailCallable(new CacheException("")));

        Callable<Forecast> forecastCallable = weatherRepository.getLastForecast();
        forecastCallable.call();
    }

    @Test
    public void shouldReturnSavedLocationsList() throws Exception {
        List<Location> expectedLocations = Collections.emptyList();
        when(localDataSource.getCities())
                .thenReturn(simpleSuccessCallable(expectedLocations));

        Callable<List<Location>> locationCallable = weatherRepository.getLocations();
        List<Location> actual = locationCallable.call();

        assertEquals(expectedLocations, actual);
    }

    @Test(expected = CacheException.class)
    public void shouldThrowCacheExceptionGettingSavedLocationsList() throws Exception {
        when(localDataSource.getCities())
                .thenReturn(this.<List<Location>>simpleFailCallable(new CacheException("")));

        Callable<List<Location>> locationsCallable = weatherRepository.getLocations();
        locationsCallable.call();
    }
}