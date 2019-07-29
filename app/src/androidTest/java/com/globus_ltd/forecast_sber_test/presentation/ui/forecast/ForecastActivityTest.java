package com.globus_ltd.forecast_sber_test.presentation.ui.forecast;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.globus_ltd.forecast_sber_test.R;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.Weather;
import com.globus_ltd.forecast_sber_test.domain.model.WeatherIcon;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;
import com.globus_ltd.forecast_sber_test.presentation.presentation.forecast.ForecastPresenter;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ForecastActivityTest {
    @Rule
    public ActivityTestRule<ForecastActivity> rule = new ActivityTestRule<>(ForecastActivity.class, true, false);
    @Mock
    ForecastPresenter presenter;
    private Date expectedDate;
    private String expectedDateString;
    private ForecastActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(2000, 10, 10, 10, 10);
        expectedDate = calendar.getTime();
        expectedDateString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                .format(expectedDate);
        ForecastActivity.setPresenter(presenter);
        rule.launchActivity(null);
        activity = rule.getActivity();
    }

    @Test
    public void shouldShowActualForecast() {
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setResult(createForecast(false));
        expected.setInProgress(false);
        String expectedTemperature = activity.getString(R.string.current_temperature, TEMPERATURE);
        String expectedMinTemperature = activity.getString(R.string.min_temperature, TEMPERATURE);
        String expectedMaxTemperature = activity.getString(R.string.max_temperature, TEMPERATURE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.progress)).check(matches(isGone));
        onView(withId(R.id.not_found)).check(matches(isGone));
        onView(withId(R.id.cachedInfo)).check(matches(isGone));

        onView(withId(R.id.locationTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.locationTitle)).check(matches(withText(LOCATION)));
        onView(withId(R.id.temperatureCurrent)).check(matches(isDisplayed()));
        onView(withId(R.id.temperatureCurrent)).check(matches(withText(expectedTemperature)));
        onView(withId(R.id.weatherDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.weatherDescription)).check(matches(withText(WEATHER_DESCRIPTION)));
        onView(withId(R.id.minTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.minTemperature)).check(matches(withText(expectedMinTemperature)));
        onView(withId(R.id.maxTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.maxTemperature)).check(matches(withText(expectedMaxTemperature)));
    }

    @Test
    public void shouldShowCachedForecast() {
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setResult(createForecast(true));
        expected.setInProgress(false);
        String expectedTemperature = activity.getString(R.string.current_temperature, TEMPERATURE);
        String expectedMinTemperature = activity.getString(R.string.min_temperature, TEMPERATURE);
        String expectedMaxTemperature = activity.getString(R.string.max_temperature, TEMPERATURE);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.progress)).check(matches(isGone));
        onView(withId(R.id.not_found)).check(matches(isGone));

        onView(withId(R.id.locationTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.locationTitle)).check(matches(withText(LOCATION)));
        onView(withId(R.id.temperatureCurrent)).check(matches(isDisplayed()));
        onView(withId(R.id.temperatureCurrent)).check(matches(withText(expectedTemperature)));
        onView(withId(R.id.weatherDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.weatherDescription)).check(matches(withText(WEATHER_DESCRIPTION)));
        onView(withId(R.id.minTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.minTemperature)).check(matches(withText(expectedMinTemperature)));
        onView(withId(R.id.maxTemperature)).check(matches(isDisplayed()));
        onView(withId(R.id.maxTemperature)).check(matches(withText(expectedMaxTemperature)));
        onView(withId(R.id.cachedInfo)).check(matches(isDisplayed()));
        onView(withId(R.id.cachedInfo)).check(matches(withText(
                activity.getString(R.string.cached, expectedDateString))));
    }

    @Test
    public void shouldShowNoForecast() {
        int expectedFailureString = R.string.error_forecast_request_failed;
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setResult(null);
        expected.setInProgress(false);
        expected.setRequestFailure(expectedFailureString);
        expected.setCurrentRequest(new ForecastRequest.ByName(""));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.progress)).check(matches(isGone));

        onView(withId(R.id.not_found)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowErrorWithRetry_whenForecastRequestFailed() {
        int expectedFailureString = R.string.error_forecast_request_failed;
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setResult(null);
        expected.setInProgress(false);
        expected.setRequestFailure(expectedFailureString);
        expected.setCurrentRequest(new ForecastRequest.ByName(""));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.progress)).check(matches(isGone));

        onView(withId(R.id.not_found)).check(matches(isDisplayed()));
        onView(withText(expectedFailureString)).check(matches(isDisplayed()));
        onView(withText(R.string.retry_action)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowLoading() {
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setResult(null);
        expected.setInProgress(true);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.progress)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldHideLocationMenuItem() {
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(true);
        expected.setInProgress(false);
        expected.setResult(createForecast(false));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.menuItemLocate)).check(doesNotExist());
    }

    @Test
    public void shouldShowLocationMenuItem() {
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setInProgress(false);
        expected.setResult(createForecast(false));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });

        onView(withId(R.id.menuItemLocate)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldShowSearchSuggestions() {
        List<String> locations = Arrays.asList("Moscow", "Mosby", "Mostly", "Azaza");
        final ForecastViewState expected = new ForecastViewState();
        expected.setGpsForbidden(false);
        expected.setInProgress(false);
        expected.setResult(createForecast(false));
        expected.setSavedLocations(locations);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.populateViewState(expected);
            }
        });
        onView(withId(R.id.menuItemSearch)).perform(ViewActions.click());
        onView(withId(androidx.appcompat.R.id.search_src_text))
                .perform(ViewActions.typeTextIntoFocusedView("Mos"));

        onView(withText("Moscow")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("Mosby")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("Mostly")).inRoot(isPlatformPopup()).check(matches(isDisplayed()));
        onView(withText("Azaza")).inRoot(isPlatformPopup()).check(doesNotExist());
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

    private static Matcher<View> isGone = withEffectiveVisibility(ViewMatchers.Visibility.GONE);

}