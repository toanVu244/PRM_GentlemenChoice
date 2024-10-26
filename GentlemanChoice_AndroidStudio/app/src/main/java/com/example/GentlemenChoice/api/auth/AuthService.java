package com.example.GentlemenChoice.api.auth;

import com.example.GentlemenChoice.model.Customer;
import com.example.GentlemenChoice.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {
    @POST("Auth")
    Call<LoginResponse> login(@Query("email") String email, @Query("password") String password);

    @POST("Customer")
    Call<Void> register(@Body Customer customer);
}
