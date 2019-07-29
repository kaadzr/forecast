package com.globus_ltd.forecast_sber_test.domain.interactor;

import com.globus_ltd.forecast_sber_test.BaseTest;
import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.CurrentThreadAppExecutors;
import com.globus_ltd.forecast_sber_test.data.model.CacheException;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.domain.repository.WeatherRepository;
import com.globus_ltd.forecast_sber_test.transport.HttpFailedException;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ForecastInteractorTest extends BaseTest {
    private ForecastInteractor interactor;
    @Mock
    WeatherRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Config.AppExecutors currentThreadExecutors = new CurrentThreadAppExecutors();
        interactor = new ForecastInteractor(
                repository,
                currentThreadExecutors
        );
    }

    @Test
    public void shouldReturnActualForecastByName() {
        String name = "name";
        final Forecast expected = mock(Forecast.class);
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(name)).thenReturn(simpleSuccessCallable(expected));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByName(name));
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldReturnCachedForecastByName() {
        String name = "name";
        final Forecast expected = mock(Forecast.class);
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(name))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(404)));
        when(repository.getCachedForecast(name)).thenReturn(simpleSuccessCallable(expected));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByName(name));
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldThrowCacheExceptionGettingForecastByName() {
        String name = "name";
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(name))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(404)));
        when(repository.getCachedForecast(name))
                .thenReturn(this.<Forecast>simpleFailCallable(new CacheException("")));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByName(name));
        call.subscribe(callback);
        call.run();

        verify(callback).onError(any(CacheException.class));
    }

    @Test
    public void shouldReturnActualForecastByCoordinates() {
        float latitude = 10f;
        float longitude = 10f;
        final Forecast expected = mock(Forecast.class);
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(latitude, longitude)).thenReturn(simpleSuccessCallable(expected));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByCoordinates(latitude, longitude));
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldReturnLastForecastByCoordinates() {
        float latitude = 10f;
        float longitude = 10f;
        final Forecast expected = mock(Forecast.class);
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(latitude, longitude))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(404)));
        when(repository.getLastForecast()).thenReturn(simpleSuccessCallable(expected));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByCoordinates(latitude, longitude));
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldThrowCacheExceptionGettingForecastByCoordinates() {
        float latitude = 10f;
        float longitude = 10f;
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getForecast(latitude, longitude))
                .thenReturn(this.<Forecast>simpleFailCallable(new HttpFailedException(404)));
        when(repository.getLastForecast())
                .thenReturn(this.<Forecast>simpleFailCallable(new CacheException("")));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.ByCoordinates(latitude, longitude));
        call.subscribe(callback);
        call.run();

        verify(callback).onError(any(CacheException.class));
    }

    @Test
    public void shouldReturnLastForecast() {
        final Forecast expected = mock(Forecast.class);
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getLastForecast()).thenReturn(simpleSuccessCallable(expected));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.Last());
        call.subscribe(callback);
        call.run();

        verify(callback).onSuccess(expected);
    }

    @Test
    public void shouldThrowCacheExceptionGettingLastForecast() {
        final Callback<Forecast> callback = (Callback<Forecast>) mock(Callback.class);
        when(repository.getLastForecast())
                .thenReturn(this.<Forecast>simpleFailCallable(new CacheException("")));

        Call<Forecast> call = interactor.getForecast(new ForecastRequest.Last());
        call.subscribe(callback);
        call.run();

        verify(callback).onError(any(CacheException.class));
    }
}