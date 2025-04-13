package com.zaicev.CloudFileStorage.security.authentication;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaicev.CloudFileStorage.dto.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsonAuthFilter extends AbstractAuthenticationProcessingFilter {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public JsonAuthFilter(String defaultFilterProcessesUrl) {
		super(new AntPathRequestMatcher(defaultFilterProcessesUrl, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		UserDTO userDTO = objectMapper.readValue(request.getInputStream(), UserDTO.class);
		
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
	}
	
}
