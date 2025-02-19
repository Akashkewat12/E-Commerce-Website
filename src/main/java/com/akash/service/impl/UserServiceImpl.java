package com.akash.service.impl;

import com.akash.config.JwtProvider;
import com.akash.modal.User;
import com.akash.repository.UserRepository;
import com.akash.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        //jwt=jwt.trim();

        String email=jwtProvider.getEmailFromJwtToken(jwt);

        return this.findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {

        User user=userRepository.findByEmail(email);

        if(user==null) {
            throw new Exception("User not found this email - " +email);
        }
        return user;
    }
}
