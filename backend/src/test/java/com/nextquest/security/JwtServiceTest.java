package com.nextquest.security;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.security.oauth2.jwt.JwsHeader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    
    @Mock 
    private JwtEncoder jwtEncoder;

    private JwtService jwtService;

    @BeforeEach 
    void setUp() {
        jwtService = new JwtService(jwtEncoder, "nextquest", Duration.ofHours(1));
    }

    @Test 
    void shouldGenerateTokenWithExpectedHeaderAndClaims() {

        Jwt encodedJwt = mock(Jwt.class);

        when(encodedJwt.getTokenValue()).thenReturn("signed-jwt");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(encodedJwt);

        Instant beforeGeneration = Instant.now();

        String token = jwtService.generateToken(7L);

        Instant afterGeneration = Instant.now();

        assertEquals("signed-jwt", token);

        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtEncoderParameters parameters = parametersCaptor.getValue();
        JwsHeader header = parameters.getJwsHeader();
        JwtClaimsSet claims = parameters.getClaims();

        assertEquals(MacAlgorithm.HS256, header.getAlgorithm());
        assertEquals("JWT", header.getType());

        assertEquals("nextquest", claims.getClaims().get("iss"));
        assertEquals("7", claims.getSubject());

        assertFalse(claims.getIssuedAt().isBefore(beforeGeneration));
        assertFalse(claims.getIssuedAt().isAfter(afterGeneration));

        assertEquals(Duration.ofHours(1), Duration.between(claims.getIssuedAt(), claims.getExpiresAt()));
    }
}
