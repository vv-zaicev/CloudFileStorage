package com.zaicev.CloudFileStorage.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.zaicev.CloudFileStorage.security.authentication.AuthenticationFailureHandlerImpl;
import com.zaicev.CloudFileStorage.security.authentication.AuthenticationSuccessHandlerImpl;
import com.zaicev.CloudFileStorage.security.authentication.JsonAuthFilter;
import com.zaicev.CloudFileStorage.security.authentication.LogoutSuccessHandlerImpl;
import com.zaicev.CloudFileStorage.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@ComponentScan("com.zaicev.CloudFileStorage.security")
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Value("${FRONTEND_URL}")
	private String frontendURL;

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
	AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
				.authenticationProvider(authenticationProvider())
				.build();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(
						auth -> auth
								.requestMatchers("/auth/sign-in", "/auth/sign-up", "/swagger-ui/**", "/v3/api-docs/**", "/OpenApiDocumentation.yaml")
								.permitAll()
								.requestMatchers(HttpMethod.OPTIONS)
								.permitAll()
								.anyRequest()
								.authenticated())
				.addFilterBefore(jsonAuthFilter(authenticationManager(httpSecurity)), UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
						.logoutUrl("/auth/sign-out")
						.logoutSuccessHandler(new LogoutSuccessHandlerImpl())
						.invalidateHttpSession(true)
						.deleteCookies("SESSION"))
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
	JsonAuthFilter jsonAuthFilter(AuthenticationManager authenticationManager) throws Exception {
		JsonAuthFilter filter = new JsonAuthFilter("/auth/sign-in");
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandlerImpl());
		filter.setAuthenticationFailureHandler(new AuthenticationFailureHandlerImpl());
		return filter;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(frontendURL));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
