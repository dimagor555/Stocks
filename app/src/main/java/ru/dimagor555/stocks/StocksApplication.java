package ru.dimagor555.stocks;

import android.app.Application;
import android.util.Log;
import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.plugins.RxJavaPlugins;

@HiltAndroidApp
public class StocksApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        RxJavaPlugins.setErrorHandler(throwable -> Log.e("RxError", throwable.getMessage(), throwable));
    }
}
