package com.arctouch.codechallenge.util.helpers

import android.util.Log
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


object RetrofitHelper {

    private var api: TmdbApi? = null

    fun getApi(): TmdbApi? {
        if (api == null) {
            rebuildRetrofit()
        }
        return api
    }

    private fun rebuildRetrofit() {

        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.d("HttpLoggingInterceptor", message) }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(httpLoggingInterceptor)
        }

        val client = builder.build()

        api = Retrofit.Builder()
                .baseUrl(TmdbApi.URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .build()
                .create(TmdbApi::class.java)
    }
}
