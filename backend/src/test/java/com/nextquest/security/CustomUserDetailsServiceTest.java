package com.nextquest.security;

import java.util.Optional;

import com.nextquest.model.User;
import com.nextquest.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    
    @Mock 
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test 
    void shouldLoadUserByEmail() {
        User storedUser = mock(User.class);

        when(storedUser.getId()).thenReturn(7L);
        when(storedUser.getEmail()).thenReturn("user@gmail.com");
        when(storedUser.getPassword()).thenReturn("encoded-password");

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(storedUser));

        AuthenticatedUser authenticatedUser = assertInstanceOf(AuthenticatedUser.class,
                userDetailsService.loadUserByUsername("user@gmail.com"));

        assertEquals(7L, authenticatedUser.getId());
        assertEquals("user@gmail.com", authenticatedUser.getUsername());
        assertEquals("encoded-password", authenticatedUser.getPassword());
        assertTrue(authenticatedUser.getAuthorities().isEmpty());

        verify(userRepository).findByEmail("user@gmail.com");
    }
    
    @Test 
    void shouldRejectUnknownEmailWithGenericMessage() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@gmail.com"));
        
        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail("unknown@gmail.com"); 
    }
}
