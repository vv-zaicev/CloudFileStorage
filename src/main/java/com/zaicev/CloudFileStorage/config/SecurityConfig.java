package com.zaicev.CloudFileStorage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import com.zaicev.CloudFileStorage.security.handlers.AuthenticationSuccessHandlerImpl;
import com.zaicev.CloudFileStorage.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@ComponentScan("com.zaicev.CloudFileStorage.security")
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsServiceImpl);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth
								.requestMatchers("/api/auth/*")
								.permitAll()
								.anyRequest()
								.authenticated())
				.formLogin(form -> form
						.loginProcessingUrl("/auth/signin")
						.successHandler(new AuthenticationSuccessHandlerImpl())
						.failureHandler((request, response, ex) -> {
							response.setStatus(401);
							response.getWriter().write("Denied");
						}))
				.logout(logout -> logout
						.logoutUrl("/auth/signout"))
				.exceptionHandling(x -> x
						.authenticationEntryPoint((request, response, authException) -> {
							response.sendError(401, "Пользователь не авторизован");
						})
						.accessDeniedHandler((request, response, accessDeniedException) -> {
							response.sendError(403, "Доступ запрещен");
						}))
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

		return httpSecurity.build();
	}

	@Bean
	SpringSessionRememberMeServices rememberMeServices() {
		SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
		rememberMeServices.setAlwaysRemember(true);
		return rememberMeServices;
	}

}
