package ru.dimagor555.stocks;

import android.app.Application;
import android.util.Log;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dimagor555.stocks.data.remote.FinnhubApi;

public class StocksApplication extends Application {
    private FinnhubApi finnhubApi;

    @Override
    public void onCreate() {
        super.onCreate();

        configureRetrofit();
        RxJavaPlugins.setErrorHandler(throwable -> Log.e("RxError", throwable.getMessage(), throwable));
    }

    private void configureRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(false)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finnhub.io/api/v1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        finnhubApi = retrofit.create(FinnhubApi.class);
    }

    public FinnhubApi getFinnhubApi() {
        return finnhubApi;
    }
}
