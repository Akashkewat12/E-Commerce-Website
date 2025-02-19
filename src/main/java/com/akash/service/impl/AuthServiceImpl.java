package com.akash.service.impl;

import com.akash.config.JwtProvider;
import com.akash.domain.USER_ROLE;
import com.akash.modal.Cart;
import com.akash.modal.User;
import com.akash.modal.VerificationCode;
import com.akash.repository.CartRepository;
import com.akash.repository.UserRepository;
import com.akash.repository.VerificationCodeRepository;
import com.akash.request.LoginRequest;
import com.akash.response.AuthResponse;
import com.akash.response.SignupRequest;
import com.akash.service.AuthService;
import com.akash.utils.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final  CustomUserServiceImpl customUserService;

    @Override
    public void sentLoginOtp(String email) throws Exception {
        String SIGNING_PREFIX="signin_";

        if(email.startsWith(SIGNING_PREFIX)) {
            email=email.substring(SIGNING_PREFIX.length());

            User user=userRepository.findByEmail(email);
            if(user==null){
                throw new Exception("user not exist with provided email");
            }
        }

        VerificationCode isExist=verificationCodeRepository.findByEmail(email);

        if(isExist != null) {
            verificationCodeRepository.delete(isExist);
        }

        String otp= OtpUtil.generateOtp();

        VerificationCode verificationCode=new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject="zosh bazaar login/signup otp";
        String text="your login/signup otp - "+otp;

        emailService.sendVerificationOtpEmail(email, otp,subject, text);

    }

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

    @Override
    public AuthResponse signing(LoginRequest req) {
        String userName=req.getEmail();
        String otp=req.getOtp();

        Authentication authentication = authenticate(userName,otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token=jwtProvider.generateToken(authentication);

        AuthResponse authResponse=new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");

        Collection<? extends  GrantedAuthority> authorities=authentication.getAuthorities();
        String roleName=authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));
        return authResponse;

    }

    private Authentication authenticate(String userName, String otp) {

        UserDetails userDetails =customUserService.loadUserByUsername(userName);

        if(userDetails == null) {
            throw  new BadCredentialsException("Invalid username");
        }

        VerificationCode verificationCode=verificationCodeRepository.findByEmail(userName);

        if(verificationCode == null || !verificationCode.getOtp().equals(otp)) {
           throw new BadCredentialsException("wrong otp");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}
