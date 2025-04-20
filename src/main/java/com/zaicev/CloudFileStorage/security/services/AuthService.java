package com.zaicev.CloudFileStorage.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.dto.UserDTO;
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

	public void registerUser(UserDTO userDTO, HttpServletRequest httpRequest) {
		createUser(userDTO);
		loginUser(userDTO, httpRequest);
	}

	private void createUser(UserDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new UsernameAlreadyTakenException(userDTO.getUsername());
		}

		User user = new User();
		user.setUsername(userDTO.getUsername());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

		userRepository.save(user);
	}

	private void loginUser(UserDTO userDTO, HttpServletRequest httpRequest) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
		HttpSession session = httpRequest.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", context);
	}

}
