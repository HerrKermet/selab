package com.example.a22b11.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.example.a22b11.db.User;

public interface FitnessApiClient {
    @POST("register.php")
    Call<RegisteredUser> register();

    @GET("account_info.php")
    Call<User> accountInfo();

    /**
     * @param loginCredentials - user id and password
     * @return session
     */
    @POST("login.php")
    @Headers("Content-Type: application/json")
    Call<Session> login(@Body LoginCredentials loginCredentials);

    @POST("logout.php")
    @Headers("Content-Type: application/json")
    Call<Void> logout(@Body Session session);

    @POST("sync.php")
    @Headers("Content-Type: application/json")
    Call<SyncObjectResponse> synchronize(@Body SyncObject syncObject);
}
