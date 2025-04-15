package com.zaicev.CloudFileStorage.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zaicev.CloudFileStorage.dto.UserDTO;
import com.zaicev.CloudFileStorage.security.services.AuthService;

@RestController
@RequestMapping("/auth")
public class SecurityController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/sign-up")
	@ResponseStatus(HttpStatus.CREATED)
	public UserDTO signup(@RequestBody UserDTO userDTO)  {
		authService.registerUser(userDTO);
		userDTO.setPassword(null);
		return userDTO;
	}
	
	@PostMapping("/sign-in")
	public UserDTO signin(@RequestBody UserDTO userDTO){
		userDTO.setPassword(null);
		return userDTO;
	}

}
