package com.nextquest.security;

import java.util.Collection;
import java.util.List;

import com.nextquest.model.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails {
    
    private final Long id;
    private final String email;
    private final String password;

    public AuthenticatedUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public Long getId() {
        return id;
    }

    @Override 
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override 
    public String getPassword() {
        return password;
    }

    @Override 
    public String getUsername() {
        return email;
    }
}
