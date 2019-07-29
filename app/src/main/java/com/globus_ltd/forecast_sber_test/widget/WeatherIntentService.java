package com.globus_ltd.forecast_sber_test.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import com.globus_ltd.forecast_sber_test.ForecastApp;
import com.globus_ltd.forecast_sber_test.R;
import com.globus_ltd.forecast_sber_test.data.repository.LocalDataSource;
import com.globus_ltd.forecast_sber_test.domain.model.Forecast;
import com.globus_ltd.forecast_sber_test.presentation.ui.forecast.ForecastActivity;

public class WeatherIntentService extends IntentService {
    private LocalDataSource localDataSource;
    private AppWidgetManager widgetManager;

    public WeatherIntentService() {
        super(WeatherIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        init();
        Forecast result = null;
        try {
            result = localDataSource.getLatestForecast().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] widgetIds = intent.getIntArrayExtra(KEY_WIDGET_IDS);
        updateWidgets(result, widgetIds);
    }

    private void init() {
        ForecastApp app = (ForecastApp) getApplication();
        if (localDataSource == null) {
            localDataSource = app.getInjector().localDataSource;
        }
        if (widgetManager == null) {
            widgetManager = (AppWidgetManager) getSystemService(APPWIDGET_SERVICE);
        }
    }

    private void updateWidgets(@Nullable Forecast result, int[] widgetIds) {
        Intent activityIntent = new Intent(this, ForecastActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, activityIntent, 0);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widgetContainer, pending);
        if (result != null) {
            views.setViewVisibility(R.id.errorText, View.GONE);
            views.setViewVisibility(R.id.content, View.VISIBLE);
            views.setTextViewText(R.id.temperatureLevel, getString(R.string.current_temperature,
                    result.getWeather().getTemperature()));
            views.setImageViewResource(R.id.weatherIcon, result.getWeather().getWeatherIcon().getIconRes());
        } else {
            views.setViewVisibility(R.id.errorText, View.VISIBLE);
            views.setViewVisibility(R.id.content, View.GONE);
        }
        widgetManager.updateAppWidget(widgetIds, views);
    }

    public static void requestWidgetsUpdate(Context context, int[] widgetIds) {
        Intent intent = new Intent(context, WeatherIntentService.class);
        intent.putExtra(KEY_WIDGET_IDS, widgetIds);
        context.startService(intent);
    }

    private static final String KEY_WIDGET_IDS = "widget_id";
}
