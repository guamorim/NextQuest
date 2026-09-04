package com.nextquest.service;

import com.nextquest.dto.CreateUserRequest;
import com.nextquest.exception.DuplicateResourceException;
import com.nextquest.model.User;
import com.nextquest.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User createUser(CreateUserRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new DuplicateResourceException("Email already registered");
		}

		User user = new User(request.getName(), request.getEmail(), request.getPassword());
		return userRepository.save(user);
	}
}
