package com.akash.service.impl;

import com.akash.config.JwtProvider;
import com.akash.domain.USER_ROLE;
import com.akash.modal.Cart;
import com.akash.modal.User;
import com.akash.modal.VerificationCode;
import com.akash.repository.CartRepository;
import com.akash.repository.UserRepository;
import com.akash.repository.VerificationCodeRepository;
import com.akash.response.SignupRequest;
import com.akash.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;


    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode=verificationCodeRepository.findByEmail(req.getEmail());

        if(verificationCode==null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Wrong otp");
        }


        User user=userRepository.findByEmail(req.getEmail());

        if(user == null) {
            User createdUser=new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("893989348");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user=userRepository.save(createdUser);

            Cart cart=new Cart();
            cart.setUser(user);
            cartRepository.save(cart);

        }

        List<GrantedAuthority> authorities=new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication=new UsernamePasswordAuthenticationToken(req.getEmail(), null,authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtProvider.generateToken(authentication);

    }
}
