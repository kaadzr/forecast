package com.globus_ltd.forecast_sber_test.presentation.ui.forecast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.globus_ltd.forecast_sber_test.ForecastApp;
import com.globus_ltd.forecast_sber_test.Injector;
import com.globus_ltd.forecast_sber_test.R;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.Location;
import com.globus_ltd.forecast_sber_test.domain.model.Weather;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;
import com.globus_ltd.forecast_sber_test.presentation.presentation.forecast.ForecastPresenter;
import com.globus_ltd.forecast_sber_test.presentation.presentation.forecast.ForecastView;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ForecastActivity extends AppCompatActivity implements ForecastView {
    private static final int REQUEST_LOCATION_PERMISSIONS = 4422;
    private Snackbar errorSnackbar = null;
    private RetryAction retryAction;
    private SuggestionAdapter searchAutocompleteAdapter;
    private DateFormat cacheTimeFormatter =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

    private View progressView;
    private TextView locationTitle;
    private TextView currentTemperature;
    private ImageView weatherIcon;
    private TextView weatherDescription;
    private TextView minTemperature;
    private TextView maxTemperature;
    private TextView pressure;
    private TextView humidity;
    private View notFoundView;
    private TextView cachedTextView;
    private MenuItem locationMenuItem;
    private MenuItem retryMenuItem;
    private View anchor;

    private View.OnClickListener retryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            retryAction.retry();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        if (presenter == null) {
            createPresenter();
        }
        findViews();
        searchAutocompleteAdapter = new SuggestionAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachView();
    }

    private void findViews() {
        progressView = findViewById(R.id.progress);
        locationTitle = findViewById(R.id.locationTitle);
        currentTemperature = findViewById(R.id.temperatureCurrent);
        weatherIcon = findViewById(R.id.weatherIcon);
        weatherDescription = findViewById(R.id.weatherDescription);
        minTemperature = findViewById(R.id.minTemperature);
        maxTemperature = findViewById(R.id.maxTemperature);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);
        notFoundView = findViewById(R.id.not_found);
        cachedTextView = findViewById(R.id.cachedInfo);
        anchor = findViewById(R.id.viewRoot);
    }

    private void createPresenter() {
        Injector injector = ((ForecastApp) getApplication()).getInjector();
        presenter = new ForecastPresenter(
                injector.forecastInteractor,
                injector.locationsListInteractor,
                injector.locationInteractor,
                injector.executors,
                new ForecastViewState()
        );
    }

    @Override
    public LocationManager getLocationManager() {
        return (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        locationMenuItem = menu.findItem(R.id.menuItemLocate);
        retryMenuItem = menu.findItem(R.id.menuItemRefresh);
        final MenuItem searchItem = menu.findItem(R.id.menuItemSearch);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.setGroupVisible(R.id.menuNonSearchGroup, true);
                invalidateOptionsMenu();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.setGroupVisible(R.id.menuNonSearchGroup, false);
                return true;
            }
        });
        setupSearch(searchItem);
        presenter.requestCurrentState();
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearch(final MenuItem searchItem) {
        final SearchView search = (SearchView) searchItem.getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.locationNameReceived(search.getQuery().toString());
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return newText.length() != 0;
            }
        });
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return locationSelected(position);
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return locationSelected(position);
            }

            private boolean locationSelected(int position) {
                Cursor cursor = (Cursor) searchAutocompleteAdapter.getItem(position);
                String location = SuggestionAdapter.getLocationFromCursor(cursor, position);
                if (location == null) {
                    location = "";
                }
                presenter.locationNameReceived(location);
                searchItem.collapseActionView();
                return true;
            }
        });
        search.setSuggestionsAdapter(searchAutocompleteAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemRefresh:
                if (retryAction != null) {
                    retryAction.retry();
                }
                return true;
            case R.id.menuItemLocate:
                presenter.requestLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void populateViewState(ForecastViewState state) {
        if (locationMenuItem != null) {
            locationMenuItem.setVisible(!state.isGpsForbidden());
        }
        setupLocations(state.getSavedLocations());
        showLoading(state.isInProgress());
        Forecast result = state.getResult();
        if (result == null) {
            showNotFound();
        } else {
            setCachingInfo(result);
            Location location = result.getLocation();
            if (location != null) {
                setLocation(location);
            }
            Weather weather = result.getWeather();
            if (weather != null) {
                setWeather(weather);
            }
        }
        ForecastRequest lastRequest = state.getCurrentRequest();
        setupRetry(lastRequest, state.getRequestFailure());
        if (state.getErrorMessage() != null
                && state.getErrorMessage().isExists()) {
            showError(getString(state.getErrorMessage().getAndErase()));
        }
    }

    private void setupRetry(ForecastRequest lastRequest, Integer requestFailure) {
        boolean hasLastRequest = lastRequest != null;
        if (hasLastRequest) {
            retryAction = new RetryAction(lastRequest);
            if (requestFailure != null) {
                showRetry(getString(requestFailure));
            } else {
                hideRetry();
            }
        }
        if (retryMenuItem != null) {
            retryMenuItem.setVisible(hasLastRequest);
        }
    }

    private void setupLocations(List<String> savedLocations) {
        if (searchAutocompleteAdapter != null) {
            searchAutocompleteAdapter.setData(
                    savedLocations == null
                            ? Collections.<String>emptyList()
                            : savedLocations
            );
        }
    }

    private void setLocation(Location location) {
        locationTitle.setText(location.getLocationName());
    }

    private void setCachingInfo(Forecast forecast) {
        if (forecast.isCached()) {
            String cacheTimeString = cacheTimeFormatter.format(forecast.getForecastDate());
            cachedTextView.setText(getString(R.string.cached, cacheTimeString));
        }
        visible(cachedTextView, forecast.isCached());
    }

    private void setWeather(Weather weather) {
        visible(notFoundView, false);
        currentTemperature.setText(getString(R.string.current_temperature, weather.getTemperature()));
        weatherIcon.setImageResource(weather.getWeatherIcon().getIconRes());
        weatherDescription.setText(weather.getWeatherDescription());
        minTemperature.setText(getString(R.string.min_temperature, weather.getMinTemperature()));
        maxTemperature.setText(getString(R.string.max_temperature, weather.getMaxTemperature()));
        pressure.setText(getString(R.string.pressure, weather.getPressure()));
        humidity.setText(getString(R.string.humidity, weather.getHumidity()));
    }

    private void showLoading(boolean show) {
        visible(progressView, show);
    }

    private void showRetry(String message) {
        if (errorSnackbar != null && errorSnackbar.isShown()) {
            errorSnackbar.dismiss();
        }
        errorSnackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.retry_action, retryClickListener);
        errorSnackbar.show();
    }

    private void hideRetry() {
        if (errorSnackbar != null && errorSnackbar.isShown()) {
            errorSnackbar.dismiss();
        }
    }

    private void showError(String message) {
        if (errorSnackbar != null && errorSnackbar.isShown()) {
            return;
        }
        errorSnackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
        errorSnackbar.show();
    }

    private void showNotFound() {
        visible(notFoundView, true);
    }

    private void visible(View view, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        view.setVisibility(visibility);
    }

    @Override
    protected void onDestroy() {
        if (!isChangingConfigurations()) {
            presenter.onDestroy();
            presenter = null;
        }
        super.onDestroy();
    }

    @Override
    public void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS);
        } else {
            presenter.locationPermissionGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.locationPermissionGranted();
            } else {
                presenter.locationPermissionForbidden();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static ForecastPresenter presenter;

    @VisibleForTesting
    public static void setPresenter(ForecastPresenter presenter) {
        ForecastActivity.presenter = presenter;
    }

    private static class RetryAction {
        @NonNull
        private final ForecastRequest request;

        RetryAction(@NonNull ForecastRequest request) {
            this.request = request;
        }

        void retry() {
            if (presenter != null) {
                presenter.retry(request);
            }
        }
    }
}
