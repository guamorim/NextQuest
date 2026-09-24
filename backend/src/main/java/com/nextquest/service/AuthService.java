package com.nextquest.service;

import com.nextquest.dto.LoginRequest;
import com.nextquest.dto.LoginResponse;
import com.nextquest.security.AuthenticatedUser;
import com.nextquest.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service 
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.getEmail(), request.getPassword()));

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        String token = jwtService.generateToken(authenticatedUser.getId());

        return new LoginResponse(token);

    }
}
