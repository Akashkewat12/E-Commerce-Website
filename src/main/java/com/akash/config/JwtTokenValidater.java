package com.akash.config;

import ch.qos.logback.core.joran.action.ActionUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.xml.crypto.KeySelector;
import java.io.IOException;
import java.security.Key;
import java.util.List;

public class JwtTokenValidater extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt=request.getHeader("Authorization");
// Bearer jwt
        if(jwt != null) {
            jwt=jwt.substring(7);
            try {
                 SecretKey key= Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());
                Claims claims= Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build().
                        parseClaimsJws(jwt)
                        .getBody();

                String emails=String.valueOf(claims.get("emails"));
                String authorities =String.valueOf(claims.get("authorities"));

                List<GrantedAuthority> auths= AuthorityUtils
                        .commaSeparatedStringToAuthorityList(authorities);
                Authentication authentication=new UsernamePasswordAuthenticationToken(emails, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid JWT token...");
            }
        }

        filterChain.doFilter(request, response);
    }


}
