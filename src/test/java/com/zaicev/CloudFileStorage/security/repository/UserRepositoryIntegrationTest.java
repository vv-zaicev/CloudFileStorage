package com.zaicev.CloudFileStorage.security.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.zaicev.CloudFileStorage.security.models.User;

@DataJpaTest
@Testcontainers
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.properties"})
public class UserRepositoryIntegrationTest {
	@Autowired
	private UserRepository userRepository;

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("testDB")
			.withUsername("testuser")
			.withPassword("testpass");
	
	@DynamicPropertySource
	static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}
	
	@Test
	void shouldSaveAndRetrieveUser() {
		User user = new User();
		user.setUsername("newUser");
		user.setPassword("password");
		
		userRepository.save(user);
		
		Optional<User> receivedUser = userRepository.findByUsername(user.getUsername());
		
		assertThat(receivedUser).isPresent();
		assertThat(receivedUser.get().getUsername()).isEqualTo(user.getUsername());
	}
	
	@Test
	void shouldReturnTrueWhenUserExists() {
		User user = new User();
		user.setUsername("existingUser");
		user.setPassword("pass");
		userRepository.save(user);
		
		boolean exists = userRepository.existsByUsername(user.getUsername());
		
		Assertions.assertTrue(exists);
	}
	
	@Test
	void shouldReturnFalseWhenUserNotExists() {
		boolean exists = userRepository.existsByUsername("nothing");
		Assertions.assertFalse(exists);
	}
}
