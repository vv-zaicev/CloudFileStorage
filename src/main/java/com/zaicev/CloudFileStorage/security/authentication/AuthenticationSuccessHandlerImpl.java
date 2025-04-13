package com.zaicev.CloudFileStorage.security.authentication;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");

		String username = authentication.getName();
		String jsonResponse = objectMapper.writeValueAsString(Collections.singletonMap("username", username));

		response.getWriter().write(jsonResponse);
	}

}
