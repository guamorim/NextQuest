package com.nextquest.service;

import com.nextquest.repository.UserRepository;
import com.nextquest.dto.CreateUserRequest;
import com.nextquest.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock 
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach 
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test 
    void shouldEncodePasswordBeforeSavingUser() {
        String rawPassword = "coxinha123";

        CreateUserRequest request = new CreateUserRequest();
        request.setName("User");
        request.setEmail("user@gmail.com");
        request.setPassword(rawPassword);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        String savedPassword = userCaptor.getValue().getPassword();

        assertNotEquals(rawPassword, savedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, savedPassword));
    }
}
