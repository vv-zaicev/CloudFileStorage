package com.zaicev.CloudFileStorage.storage.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Repository
public class MinIORepository {
	
	@Autowired
	private MinioClient minioClient;
	
	private static final String USER_BUCKET_NAME="user-files";
	
	public void downloadFile(String path, int userId, MultipartFile file) throws Exception {
		String fullPath = String.format("/user-%d-files/%s%s", userId, path, file.getOriginalFilename());
		minioClient.putObject(PutObjectArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.object(fullPath)
				.stream(file.getInputStream(), file.getSize(), -1)
				.build());
	}
}
