package com.zaicev.CloudFileStorage.security.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaicev.CloudFileStorage.dto.UserDTO;
import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;

@RestController
@RequestMapping("/user")
public class UserController {
	@GetMapping("/me")
	public UserDTO getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(userDetailsImpl.getUsername());
		return userDTO;	
	}
}
