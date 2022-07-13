package com.example.a22b11.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.example.a22b11.db.User;

public interface FitnessApiClient {
    @POST("register.php")
    Call<User> register();

    @GET("account_info.php")
    Call<User> accountInfo();

    @Headers("Content-Type: application/json")
    @POST("login.php")
    Call<Void> login(@Body User user);

    @POST("logout.php")
    Call<Void> logout();

    @Headers("Content-Type: application/json")
    @POST("sync.php")
    Call<SyncObject> synchronize(@Body SyncObject syncObject);
}
