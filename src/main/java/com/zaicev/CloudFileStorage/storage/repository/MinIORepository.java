package com.zaicev.CloudFileStorage.storage.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.zaicev.CloudFileStorage.storage.exception.StorageObjectNotFound;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.SnowballObject;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MinIORepository {

	@Autowired
	private MinioClient minioClient;

	private static final String USER_BUCKET_NAME = "user-files";

	public void uploadFile(String path, MultipartFile file) throws IOException, MinioException, GeneralSecurityException {
		try (InputStream inputStream = file.getInputStream()) {
			minioClient.putObject(PutObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(path)
					.stream(inputStream, file.getSize(), -1)
					.build());
		} catch (Exception e) {
			log.error("File was not uploaded");
			log.error(e.getMessage());
		}

	}

	public long getObjectSize(String path) throws IOException, MinioException, GeneralSecurityException {
		try {
			StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(path)
					.build());
			return stat.size();
		} catch (ErrorResponseException e) {
			if (e.errorResponse().code().equals("NoSuchKey")) {
				log.warn("StorageObjectNotFound: {}", path);
				throw new StorageObjectNotFound(path);
			}
			throw e;
		}
	}

	public boolean isObjectExist(String path) throws IOException, MinioException, GeneralSecurityException {
		try {
			log.warn("object is exist");
			StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(path)
					.build());
			return true;
		} catch (ErrorResponseException e) {
			if (e.errorResponse().code().equals("NoSuchKey")) {
				log.warn("StorageObjectNotFound: {}", path);
			}
			return false;
		}
	}

	public InputStream getObject(String path) throws IOException, MinioException, GeneralSecurityException {
		try {
			InputStream stream = minioClient.getObject(GetObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(path)
					.build());
			return stream;
		} catch (ErrorResponseException e) {
			if (e.errorResponse().code().equals("NoSuchKey")) {
				log.warn("StorageObjectNotFound: {}", path);
				throw new StorageObjectNotFound(path);
			}
			throw e;
		}
	}

	public void deleteObject(String path) throws IOException, MinioException, GeneralSecurityException {
		try {
			minioClient.removeObject(RemoveObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(path)
					.build());
		} catch (ErrorResponseException e) {
			if (e.errorResponse().code().equals("NoSuchKey")) {
				log.warn("StorageObjectNotFound: {}", path);
				throw new StorageObjectNotFound(path);
			}
			throw e;
		}
	}

	public boolean isFolderExists(String path) throws IOException, MinioException, GeneralSecurityException{
		Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.prefix(path)
				.recursive(false)
				.maxKeys(1)
				.build());
		return results.iterator().hasNext();
	}

	public void copyObject(String sourcePath, String copyPath) throws IOException, MinioException, GeneralSecurityException {
		try {
			minioClient.copyObject(CopyObjectArgs.builder()
					.bucket(USER_BUCKET_NAME)
					.object(copyPath)
					.source(CopySource.builder()
							.bucket(USER_BUCKET_NAME)
							.object(sourcePath)
							.build())
					.build());
		} catch (ErrorResponseException e) {
			if (e.errorResponse().code().equals("NoSuchKey")) {
				log.warn("StorageObjectNotFound: {}", sourcePath);
				throw new StorageObjectNotFound(sourcePath);
			}
			throw e;
		}
	}

	public void moveObject(String sourcePath, String targetPath) throws IOException, MinioException, GeneralSecurityException {
		copyObject(sourcePath, targetPath);
		deleteObject(sourcePath);
	}

	public Iterable<Result<Item>> getFiles(String path, boolean recursive) {
		return minioClient.listObjects(ListObjectsArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.recursive(recursive)
				.prefix(path)
				.build());
	}

	public void uploadFolder(List<SnowballObject> objects) throws IOException, MinioException, GeneralSecurityException {
		minioClient.uploadSnowballObjects(UploadSnowballObjectsArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.objects(objects)
				.build());
	}

	public Iterable<Result<DeleteError>> deleteObjects(List<DeleteObject> objects) {
		return minioClient.removeObjects(RemoveObjectsArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.objects(objects)
				.build());
	}

	public void createFolder(String fullPath) throws IOException, MinioException, GeneralSecurityException {
		minioClient.putObject(PutObjectArgs.builder()
				.bucket(USER_BUCKET_NAME)
				.object(fullPath)
				.stream(new ByteArrayInputStream(new byte[] {}), 0, -1)
				.build());
	}

}
