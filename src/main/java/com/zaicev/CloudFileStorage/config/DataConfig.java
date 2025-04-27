package com.zaicev.CloudFileStorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class DataConfig {
	@Value("${MINIO_SECRET_KEY}")
	private String secretToken;

	@Value("${MINIO_ACCESS_KEY}")
	private String accessToken;
	
	@Value("${MINIO_HOST}")
	private String minioHost;

	@Bean
	MinioClient minioClient() {
		return MinioClient
				.builder()
				.endpoint(minioHost, 9000, false)
				.credentials(accessToken, secretToken)
				.build();
	}
}
