package com.globus_ltd.forecast_sber_test.presentation.presentation.forecast;

import android.location.LocationManager;

import com.globus_ltd.forecast_sber_test.BaseTest;
import com.globus_ltd.forecast_sber_test.BlockingCall;
import com.globus_ltd.forecast_sber_test.Config;
import com.globus_ltd.forecast_sber_test.CurrentThreadAppExecutors;
import com.globus_ltd.forecast_sber_test.R;
import com.globus_ltd.forecast_sber_test.data.model.CacheException;
import com.globus_ltd.forecast_sber_test.domain.interactor.ForecastInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationInteractor;
import com.globus_ltd.forecast_sber_test.domain.interactor.LocationsInteractor;
import com.globus_ltd.forecast_sber_test.domain.model.Coordinates;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.LowAccuracyException;
import com.globus_ltd.forecast_sber_test.domain.model.Weather;
import com.globus_ltd.forecast_sber_test.domain.model.WeatherIcon;
import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;
import com.globus_ltd.forecast_sber_test.transport.model.Call;
import com.globus_ltd.forecast_sber_test.transport.model.Callback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ForecastPresenterTest extends BaseTest {
    @Mock
    ForecastInteractor forecastInteractor;
    @Mock
    LocationsInteractor locationsInteractor;
    @Mock
    LocationInteractor locationInteractor;
    @Mock
    ForecastView view;
    private Config.AppExecutors executors = new CurrentThreadAppExecutors();
    private ForecastPresenter presenter;
    private Date expectedDate = new Date();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(forecastInteractor.getForecast(any(ForecastRequest.class))).thenReturn(this.<Forecast>mockCall());
        when(view.getLocationManager()).thenReturn(mock(LocationManager.class));
        when(locationsInteractor.getLocations()).thenReturn(this.<List<Location>>mockCall());
    }

    private void createPresenter(ForecastViewState initialState) {
        presenter = new ForecastPresenter(
                forecastInteractor,
                locationsInteractor,
                locationInteractor,
                executors,
                initialState
        );
    }

    @Test
    public void shouldRequestLocationPermission_whenStarts() {
        createPresenter(new ForecastViewState());

        presenter.attachView(view);

        verify(view).requestLocationPermissions();
    }

    @Test
    public void shouldNotRequestLocationPermission_whenResumes() {
        createPresenter(new ForecastViewState());

        presenter.attachView(view);

        verify(view, times(1)).requestLocationPermissions();
    }

    @Test
    public void shouldRequestSavedLocation_onAttach() {
        createPresenter(new ForecastViewState());
        Call<List<Location>> mock = mockCall();
        when(locationsInteractor.getLocations()).thenReturn(mock);

        presenter.attachView(view);

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldSendCurrentState_onAttach() {
        ForecastViewState expected = new ForecastViewState();
        createPresenter(expected);

        presenter.attachView(view);

        verify(view).populateViewState(expected);
    }

    @Test
    public void shouldUpdateState_whenCurrentLocationsReceived() {
        final List<Location> locations = Collections.singletonList(new Location(1, "location"));
        final String expectedLocation = "location";
        createPresenter(new ForecastViewState());
        when(locationsInteractor.getLocations()).thenReturn(new BlockingCall<>(locations));

        presenter.attachView(view);

        verify(view, atLeastOnce()).populateViewState(argThat(
                new ArgumentMatcher<ForecastViewState>() {
                    @Override
                    public boolean matches(ForecastViewState argument) {
                        List<String> actual = argument.getSavedLocations();
                        return actual != null
                                && actual.size() == 1
                                && expectedLocation.equals(actual.get(0));
                    }
                }
        ));
    }

    @Test
    public void shouldRequestLocation_whenPermissionGranted() {
        createPresenter(new ForecastViewState());
        Call<Coordinates> mock = mockCall();
        when(locationInteractor.getLocation(any(LocationManager.class))).thenReturn(mock);
        presenter.attachView(view);
        presenter.locationPermissionGranted();

        checkCallSubscribedAndRan(mock);
        verify(view, atLeastOnce()).populateViewState(
                argThat(
                        new ArgumentMatcher<ForecastViewState>() {
                            @Override
                            public boolean matches(ForecastViewState argument) {
                                return argument.isInProgress();
                            }
                        }
                )
        );
    }

    @Test
    public void shouldShowError_andSetGpsForbidden_whenPermissionNotGranted() {
        createPresenter(new ForecastViewState());

        presenter.attachView(view);
        presenter.locationPermissionForbidden();

        verify(view, atLeastOnce()).populateViewState(argThat(
                new ArgumentMatcher<ForecastViewState>() {
                    @Override
                    public boolean matches(ForecastViewState argument) {
                        return argument.isGpsForbidden()
                                && argument.getErrorMessage() != null
                                && argument.getErrorMessage().isExists();
                    }
                }
        ));
    }

    @Test
    public void shouldRequestLastForecast_whenPermissionNotGranted() {
        createPresenter(new ForecastViewState());
        Call<Forecast> mock = mockCall();
        when(forecastInteractor.getForecast(any(ForecastRequest.Last.class))).thenReturn(mock);

        presenter.attachView(view);
        presenter.locationPermissionForbidden();

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldRequestForecast_whenLocationReceived() {
        createPresenter(new ForecastViewState());
        Call<Forecast> mock = mockCall();
        when(forecastInteractor.getForecast(any(ForecastRequest.ByCoordinates.class)))
                .thenReturn(mock);
        when(locationInteractor.getLocation(any(LocationManager.class)))
                .thenReturn(new BlockingCall<>(new Coordinates(10, 10)));

        presenter.attachView(view);
        presenter.locationPermissionGranted();

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldRequestLastForecast_whenLocationRequestFailed() {
        createPresenter(new ForecastViewState());
        Call<Forecast> mock = mockCall();
        when(forecastInteractor.getForecast(any(ForecastRequest.Last.class)))
                .thenReturn(mock);
        when(locationInteractor.getLocation(any(LocationManager.class)))
                .thenReturn(new BlockingCall<Coordinates>(new LowAccuracyException()));

        presenter.attachView(view);
        presenter.locationPermissionGranted();

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldRequestForecast_whenLocationNameReceived() {
        createPresenter(new ForecastViewState());
        String name = "name";
        ForecastRequest.ByName expected = new ForecastRequest.ByName(name);
        Call<Forecast> mock = mockCall();
        when(forecastInteractor.getForecast(expected)).thenReturn(mock);

        presenter.attachView(view);
        presenter.locationNameReceived(name);

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldRequestLocation_whenMenuActionClicked() {
        createPresenter(new ForecastViewState());
        Call<Coordinates> mock = mockCall();
        when(locationInteractor.getLocation(any(LocationManager.class))).thenReturn(mock);

        presenter.attachView(view);
        presenter.requestLocation();

        checkCallSubscribedAndRan(mock);
    }


    @Test
    public void shouldRequestForecast_whenRetry() {
        createPresenter(new ForecastViewState());
        ForecastRequest.ByName request = new ForecastRequest.ByName("");
        Call<Forecast> mock = mockCall();
        when(forecastInteractor.getForecast(request)).thenReturn(mock);

        presenter.attachView(view);
        presenter.retry(request);

        checkCallSubscribedAndRan(mock);
    }

    @Test
    public void shouldSendCurrentState_ifRequested() {
        ForecastViewState expected = new ForecastViewState();
        expected.setInProgress(false);
        expected.setRequestFailure(R.string.app_name);
        expected.setCurrentRequest(new ForecastRequest.ByName("name"));
        createPresenter(expected);

        presenter.attachView(view);
        presenter.requestCurrentState();

        verify(view, atLeastOnce()).populateViewState(expected);
    }

    @Test
    public void shouldUnsubscribe_whenDestroys() {
        createPresenter(new ForecastViewState());
        Call<Forecast> forecastCall = mockCall();
        Call<Coordinates> locationCall = mockCall();
        Call<List<Location>> locationsListCall = mockCall();

        when(forecastInteractor.getForecast(any(ForecastRequest.class))).thenReturn(forecastCall);
        when(locationInteractor.getLocation(any(LocationManager.class))).thenReturn(locationCall);
        when(locationsInteractor.getLocations()).thenReturn(locationsListCall);

        presenter.attachView(view);
        presenter.requestLocation();
        presenter.retry(new ForecastRequest.ByName(""));
        presenter.onDestroy();

        checkCallUnsubscribedAndCanceled(forecastCall);
        checkCallUnsubscribedAndCanceled(locationCall);
        checkCallUnsubscribedAndCanceled(locationsListCall);
    }

    @Test
    public void shouldReturnCachedForecast_andShowError_whenForecastRequestFailed() {
        createPresenter(new ForecastViewState());
        final Forecast expected = createForecast(true);
        when(forecastInteractor.getForecast(any(ForecastRequest.ByName.class)))
                .thenReturn(new BlockingCall<>(expected));

        presenter.attachView(view);
        presenter.locationNameReceived("");

        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.isInProgress();
            }
        }));
        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.getRequestFailure() != null
                        && R.string.error_forecast_request_failed == argument.getRequestFailure()
                        && expected.equals(argument.getResult())
                        && !argument.isInProgress();
            }
        }));
    }

    @Test
    public void shouldShowError_whenForecastRequestFailed_andNoCachedForecast() {
        createPresenter(new ForecastViewState());
        when(forecastInteractor.getForecast(any(ForecastRequest.ByName.class)))
                .thenReturn(new BlockingCall<Forecast>(new CacheException("")));

        presenter.attachView(view);
        presenter.retry(new ForecastRequest.ByName(""));

        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.isInProgress();
            }
        }));
        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.getRequestFailure() != null
                        && R.string.error_forecast_request_failed == argument.getRequestFailure()
                        && argument.getResult() == null
                        && !argument.isInProgress();
            }
        }));
    }

    @Test
    public void shouldNotShowRetry_whenLastForecastRequestFailed() {
        createPresenter(new ForecastViewState());
        when(forecastInteractor.getForecast(any(ForecastRequest.Last.class)))
                .thenReturn(new BlockingCall<Forecast>(new CacheException("")));

        presenter.attachView(view);
        presenter.locationPermissionForbidden();

        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.getRequestFailure() != null
                        && R.string.error_forecast_request_failed == argument.getRequestFailure()
                        && argument.getResult() == null
                        && argument.getCurrentRequest() == null
                        && !argument.isInProgress();
            }
        }));
    }
    @Test
    public void shouldShowRetry_whenRealForecastRequestFailed() {
        createPresenter(new ForecastViewState());
        final ForecastRequest.ByName expected = new ForecastRequest.ByName("");
        when(forecastInteractor.getForecast(expected))
                .thenReturn(new BlockingCall<Forecast>(new CacheException("")));

        presenter.attachView(view);
        presenter.retry(expected);

        verify(view, atLeastOnce()).populateViewState(argThat(new ArgumentMatcher<ForecastViewState>() {
            @Override
            public boolean matches(ForecastViewState argument) {
                return argument.getRequestFailure() != null
                        && R.string.error_forecast_request_failed == argument.getRequestFailure()
                        && argument.getResult() == null
                        && argument.getCurrentRequest() == expected
                        && !argument.isInProgress();
            }
        }));
    }

    private void checkCallSubscribedAndRan(Call<?> mock) {
        verify(mock).subscribe(any((Callback.class)));
        verify(mock).run();
    }

    private void checkCallUnsubscribedAndCanceled(Call<?> call) {
        verify(call, atLeastOnce()).unsubscribe(any(Callback.class));
        verify(call, atLeastOnce()).cancel();
    }

    private <T> Call<T> mockCall() {
        return (Call<T>) mock(Call.class);
    }

    private Forecast createForecast(boolean cached) {
        return new Forecast(
                new Location(1, LOCATION),
                new Weather(ICON, WEATHER_DESCRIPTION,
                        TEMPERATURE, TEMPERATURE,
                        TEMPERATURE, PRESSURE, HUMIDITY),
                expectedDate,
                cached);
    }

    private static final String LOCATION = "Location";
    private static final float TEMPERATURE = 10f;
    private static final int PRESSURE = 1000;
    private static final int HUMIDITY = 77;
    private static final WeatherIcon ICON = WeatherIcon.BROKEN_CLOUDS;
    private static final String WEATHER_DESCRIPTION = "Broken clouds";
}