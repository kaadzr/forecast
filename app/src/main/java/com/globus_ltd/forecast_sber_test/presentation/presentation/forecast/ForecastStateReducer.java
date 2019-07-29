package com.globus_ltd.forecast_sber_test.presentation.presentation.forecast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.globus_ltd.forecast_sber_test.R;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.presentation.model.SingleLiveEvent;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.presentation.model.forecast.ForecastViewState;

import java.util.List;

class ForecastStateReducer {
    @StringRes
    private static final int LOCATION_FORBIDDEN = R.string.error_location_forbidden;
    @StringRes
    private static final int LOCATION_REQUEST_FAILED = R.string.error_location_request;
    @StringRes
    private static final int FORECAST_REQUEST_FAILED = R.string.error_forecast_request_failed;

    static ForecastViewState onForecastReceived(@NonNull ForecastViewState currentState,
                                                @NonNull Forecast forecast) {
        ForecastViewState result = currentState.copy();
        result.setInProgress(false);
        result.setRequestFailure(
                forecast.isCached()
                        ? FORECAST_REQUEST_FAILED
                        : null
        );
        result.setResult(forecast);
        return result;
    }

    static ForecastViewState onJobStarted(@NonNull ForecastViewState currentState,
                                          @NonNull ForecastRequest request) {
        ForecastViewState result = currentState.copy();
        result.setInProgress(true);
        result.setRequestFailure(null);
        if (request.isReal()) {
            result.setCurrentRequest(request);
        }
        return result;
    }

    static ForecastViewState onForecastRequestFailed(@NonNull ForecastViewState currentState) {
        ForecastViewState result = currentState.copy();
        result.setInProgress(false);
        result.setRequestFailure(FORECAST_REQUEST_FAILED);
        return result;
    }

    static ForecastViewState onLocationRequestFailed(@NonNull ForecastViewState currentState) {
        ForecastViewState result = currentState.copy();
        result.setInProgress(false);
        result.setErrorMessage(new SingleLiveEvent<>(LOCATION_REQUEST_FAILED));
        return result;
    }

    static ForecastViewState onLocationRequestStarted(@NonNull ForecastViewState currentState) {
        ForecastViewState result = currentState.copy();
        result.setInProgress(true);
        return result;
    }

    static ForecastViewState onSavedLocationsReceived(@NonNull ForecastViewState currentState,
                                                      @NonNull List<String> locations) {
        ForecastViewState result = currentState.copy();
        result.setSavedLocations(locations);
        return result;
    }

    static ForecastViewState onLocationForbidden(@NonNull ForecastViewState currentState) {
        ForecastViewState result = currentState.copy();
        result.setGpsForbidden(true);
        result.setErrorMessage(new SingleLiveEvent<>(LOCATION_FORBIDDEN));
        return result;
    }
}
