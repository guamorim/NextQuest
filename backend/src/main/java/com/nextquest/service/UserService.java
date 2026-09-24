package com.nextquest.service;

import com.nextquest.dto.CreateUserRequest;
import com.nextquest.dto.UserResponse;
import com.nextquest.exception.DuplicateResourceException;
import com.nextquest.model.User;
import com.nextquest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;

	}

	public UserResponse createUser(CreateUserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already registered");
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User(request.getName(), request.getEmail(), encodedPassword);
		
		User savedUser = userRepository.save(user);

		return new UserResponse(
				savedUser.getId(),
				savedUser.getName(),
				savedUser.getEmail());
	}
}
