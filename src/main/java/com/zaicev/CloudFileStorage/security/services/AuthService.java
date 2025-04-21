package com.zaicev.CloudFileStorage.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.dto.UserRequestDTO;
import com.zaicev.CloudFileStorage.security.exceptions.UsernameAlreadyTakenException;
import com.zaicev.CloudFileStorage.security.models.User;
import com.zaicev.CloudFileStorage.security.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	public void registerUser(User user, HttpServletRequest httpRequest) {
		createUser(user);
		loginUser(user, httpRequest);
	}

	private void createUser(User user) {
		if (userRepository.existsByUsername(user.getUsername())) {
			throw new UsernameAlreadyTakenException(user.getUsername());
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);
	}

	private void loginUser(User user, HttpServletRequest httpRequest) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
		HttpSession session = httpRequest.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", context);
	}

}
