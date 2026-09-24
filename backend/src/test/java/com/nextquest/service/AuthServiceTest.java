package com.nextquest.service;

import com.nextquest.dto.LoginRequest;
import com.nextquest.dto.LoginResponse;
import com.nextquest.security.AuthenticatedUser;
import com.nextquest.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtService);
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@gmail.com");
        request.setPassword("coxinha123");

        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        Authentication authenticatedResult = mock(Authentication.class);

        when(authenticatedResult.getPrincipal()).thenReturn(authenticatedUser);
        when(authenticatedUser.getId()).thenReturn(7L);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticatedResult);

        when(jwtService.generateToken(7L)).thenReturn("signed-jwt");

        LoginResponse response = authService.login(request);

        assertEquals("signed-jwt", response.getToken());

        ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);

        verify(authenticationManager).authenticate(authenticationCaptor.capture());

        Authentication submittedAuthentication = authenticationCaptor.getValue();

        assertEquals("user@gmail.com", submittedAuthentication.getPrincipal());
        assertEquals("coxinha123", submittedAuthentication.getCredentials());
        assertFalse(submittedAuthentication.isAuthenticated());

        verify(jwtService).generateToken(7L);
    }

    @Test 
    void shouldNotGenerateTokenWhenCredentialsAreInvalid() {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@gmail.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verifyNoInteractions(jwtService);
    }
}