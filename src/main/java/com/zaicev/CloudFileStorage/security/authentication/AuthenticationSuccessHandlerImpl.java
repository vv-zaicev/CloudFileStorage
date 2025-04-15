package com.zaicev.CloudFileStorage.security.authentication;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		HttpSession session = request.getSession(true);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");

		String username = authentication.getName();
		String jsonResponse = objectMapper.writeValueAsString(Collections.singletonMap("username", username));

		response.getWriter().write(jsonResponse);
	}

}
