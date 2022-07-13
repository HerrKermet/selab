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
    @Headers("Content-Type: application/json")
    Call<Session> login(@Body LoginCredentials loginCredentials);

    @POST("logout.php")
    Call<Void> logout(@Body Session session);

    @Headers("Content-Type: application/json")
    @POST("sync.php")
    Call<SyncObjectResponse> synchronize(@Body SyncObject syncObject);
}
