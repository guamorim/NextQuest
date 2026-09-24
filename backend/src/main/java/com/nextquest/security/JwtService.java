package com.nextquest.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

@Service 
public class JwtService {
    
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration expiration;

    public JwtService(JwtEncoder jwtEncoder, @Value("${security.jwt.issuer}") String issuer, 
            @Value("${security.jwt.expiration}") Duration expiration) {

        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiration = expiration;
    }
    
    public String generateToken(Long userId) {
        Instant now = Instant.now();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

        // Use the user ID as the subject so resource ownership comes from authentication,
        // rather than from a client-supplied userId.
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(issuer).subject(userId.toString())
                .issuedAt(now).expiresAt(now.plus(expiration)).build();
        
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
