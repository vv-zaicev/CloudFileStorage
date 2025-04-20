package com.zaicev.CloudFileStorage.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.CloudFileStorage.dto.UserDTO;
import com.zaicev.CloudFileStorage.security.exceptions.UsernameAlreadyTakenException;
import com.zaicev.CloudFileStorage.security.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class SecurityController {

	@Autowired
	private AuthService authService;

	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.CREATED)
	public UserDTO signup(@RequestBody UserDTO userDTO, HttpServletRequest httpServletRequest) throws Exception {
		try {
			authService.registerUser(userDTO, httpServletRequest);
			userDTO.setPassword(null);
			return userDTO;
		} catch (UsernameAlreadyTakenException e) {
			log.warn("username taked");
			throw new ResponseStatusException(HttpStatus.CONFLICT);
		}

	}

	@PostMapping("/sign-in")
	public UserDTO signin(@RequestBody UserDTO userDTO) {
		userDTO.setPassword(null);
		return userDTO;
	}

}
