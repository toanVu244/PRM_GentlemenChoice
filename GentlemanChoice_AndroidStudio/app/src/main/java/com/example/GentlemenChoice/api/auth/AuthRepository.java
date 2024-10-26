package com.example.GentlemenChoice.api.auth;

import com.example.GentlemenChoice.api.APIClient;


public class AuthRepository {
    public static AuthService getAuthService(){
        return APIClient.getClient().create(AuthService.class);
    }
}
