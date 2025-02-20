package com.akash.service;

import com.akash.modal.User;
import org.springframework.stereotype.Service;

public interface UserService {

    User findUserByJwtToken(String jwt) throws Exception;
    User findUserByEmail(String email) throws Exception;
}
