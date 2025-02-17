package com.akash.service;

import com.akash.request.LoginRequest;
import com.akash.response.AuthResponse;
import com.akash.response.SignupRequest;

public interface AuthService {

    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req);
}
