package ru.dimagor555.stocks.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.dimagor555.stocks.data.remote.FinnhubApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {
    @Provides
    @Singleton
    fun provideFinnhubApi(): FinnhubApi {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val apiTokenHeadersInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val newRequest = request.newBuilder()
                    .headers(request.headers)
                    .addHeader("X-Finnhub-Token", "c1duanf48v6sjvgft40g")
                    .build()
                return chain.proceed(request = newRequest)
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(apiTokenHeadersInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .retryOnConnectionFailure(false)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(FinnhubApi::class.java)
    }
}