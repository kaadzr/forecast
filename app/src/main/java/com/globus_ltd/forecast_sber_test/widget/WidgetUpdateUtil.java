package com.globus_ltd.forecast_sber_test.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import static android.content.Context.APPWIDGET_SERVICE;

public class WidgetUpdateUtil {
    private Context appContext;
    private AppWidgetManager widgetManager;

    public WidgetUpdateUtil(Context appContext) {
        this.appContext = appContext;
        this.widgetManager = (AppWidgetManager) appContext.getSystemService(APPWIDGET_SERVICE);
    }

    public void updateWidgets() {
        int[] widgetIds = widgetManager.getAppWidgetIds(new ComponentName(appContext, WeatherWidgetProvider.class));
        WeatherIntentService.requestWidgetsUpdate(appContext, widgetIds);
    }
}
