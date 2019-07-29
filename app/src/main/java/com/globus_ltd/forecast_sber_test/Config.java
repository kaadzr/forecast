package com.globus_ltd.forecast_sber_test;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.globus_ltd.forecast_sber_test.data.source.local.DbHelper;
import com.globus_ltd.forecast_sber_test.transport.HttpClient;
import com.globus_ltd.forecast_sber_test.widget.WidgetUpdateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Config {
    private final int httpTimeout = 10 * 1000;
    final AppExecutors executors = new AppExecutorsImpl();
    public final Gson gson = new GsonBuilder().create();
    final HttpClient client = new HttpClient(httpTimeout);
    final DbHelper dbHelper;
    final WidgetUpdateUtil widgetUpdateUtil;

    Config(Context appContext) {
        dbHelper = new DbHelper(appContext);
        widgetUpdateUtil = new WidgetUpdateUtil(appContext);
    }

    public interface AppExecutors {
        ExecutorService getIo();

        Executor getMain();
    }

    public static class AppExecutorsImpl implements AppExecutors {
        private final ExecutorService io = Executors.newCachedThreadPool();
        private final Executor main = new MainThreadExecutor();

        @Override
        public ExecutorService getIo() {
            return io;
        }

        @Override
        public Executor getMain() {
            return main;
        }
    }

    static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable r) {
            handler.post(r);
        }
    }
}
