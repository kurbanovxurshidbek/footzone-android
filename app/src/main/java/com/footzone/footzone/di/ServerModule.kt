package com.footzone.footzone.di

import com.footzone.footzone.networking.service.ApiService
import com.footzone.footzone.utils.KeyValues.USER_TOKEN
import com.footzone.footzone.utils.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServerModule {
    //    private val BASE_URL: String = "https://footzone-server.herokuapp.com/api/v1/"
    private val BASE_URL: String = "http://192.168.1.4:8081/api/v1/"

    @Provides
    @Singleton
    fun getRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun getApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun getClient(sharedPref: SharedPref): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        // .addInterceptor(ChuckerInterceptor(context))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(Interceptor { chain ->
            val builder = chain.request().newBuilder()
            if (sharedPref.getUserToken(USER_TOKEN, "").isNotEmpty()) {
                builder.addHeader(
                    "Authorization",
                    "Bearer ${sharedPref.getUserToken(USER_TOKEN, "")}"
                )
            }
//            builder.addHeader("Content-Type", "application/json")
//            builder.addHeader("Accept", "application/json")
            chain.proceed(builder.build())
        })
        .build()
}