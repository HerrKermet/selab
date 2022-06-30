package com.example.a22b11.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.a22b11.MyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thomasbouvier.persistentcookiejar.ClearableCookieJar;
import com.thomasbouvier.persistentcookiejar.PersistentCookieJar;
import com.thomasbouvier.persistentcookiejar.cache.SetCookieCache;
import com.thomasbouvier.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.time.Instant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FitnessApiClientBuilder {
    public static FitnessApiClient build() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        SharedPreferences cookieSharedPreferences = MyApplication.getInstance().getSharedPreferences("PersistentCookies", Context.MODE_PRIVATE);

        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(cookieSharedPreferences));

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fitness.nandlab.com/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(FitnessApiClient.class);
    }
}
