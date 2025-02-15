package com.akash.service;

import com.akash.response.SignupRequest;

public interface AuthService {

    String createUser(SignupRequest req) throws Exception;
}
