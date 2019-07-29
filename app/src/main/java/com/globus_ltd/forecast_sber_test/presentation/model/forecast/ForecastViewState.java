package com.globus_ltd.forecast_sber_test.presentation.model.forecast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.domain.model.ForecastRequest;
import com.globus_ltd.forecast_sber_test.presentation.model.SingleLiveEvent;

import java.util.List;

public class ForecastViewState implements Cloneable {
    private boolean isInProgress = true;
    private ForecastRequest currentRequest = null;
    private List<String> savedLocations = null;
    private Forecast result = null;
    private Integer requestFailure = null;
    private SingleLiveEvent<Integer> errorMessage = null;
    private boolean isGpsForbidden = false;

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    public ForecastRequest getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(ForecastRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    public List<String> getSavedLocations() {
        return savedLocations;
    }

    public void setSavedLocations(@NonNull List<String> savedLocations) {
        this.savedLocations = savedLocations;
    }

    public Forecast getResult() {
        return result;
    }

    public void setResult(Forecast result) {
        this.result = result;
    }

    @StringRes
    public Integer getRequestFailure() {
        return requestFailure;
    }

    public void setRequestFailure(@StringRes Integer stringRes) {
        this.requestFailure = stringRes;
    }

    public SingleLiveEvent<Integer> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(SingleLiveEvent<Integer> errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isGpsForbidden() {
        return isGpsForbidden;
    }

    public void setGpsForbidden(boolean gpsForbidden) {
        isGpsForbidden = gpsForbidden;
    }

    public ForecastViewState copy() {
        ForecastViewState newState = new ForecastViewState();
        newState.setInProgress(isInProgress);
        newState.setCurrentRequest(currentRequest);
        newState.setSavedLocations(savedLocations);
        newState.setResult(result);
        newState.setRequestFailure(requestFailure);
        newState.setErrorMessage(errorMessage);
        newState.setGpsForbidden(isGpsForbidden);
        return newState;
    }

    @NonNull
    @Override
    public String toString() {
        return "ForecastViewState { isInProgress = " + isInProgress +
                ", currentRequest = " + (currentRequest == null ? "null" : currentRequest.toString()) +
                ", savedLocations = " + (savedLocations==null?"null":savedLocations.toString()) +
                ", result = " + (result == null ? "null" : result.toString()) +
                ", requestFailure = " + (requestFailure == null ? "null" : requestFailure.toString()) +
                ", errorMessage = " + (errorMessage == null ? "null" : errorMessage.isExists()) +
                ", isGpsForbidden = " + isGpsForbidden + " }";
    }
}
