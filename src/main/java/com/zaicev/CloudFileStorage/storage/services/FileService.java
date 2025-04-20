package com.zaicev.CloudFileStorage.storage.services;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.zaicev.CloudFileStorage.storage.exception.StorageObjectExist;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectNotFound;
import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.repository.MinIORepository;

import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {
	@Autowired
	private MinIORepository minIORepository;

	@Autowired
	private PathService pathService;

	public StorageObject getFileInfo(String path) throws IOException, MinioException, GeneralSecurityException {
		StorageObject storageObject = pathService.getStorageObjectFromFullPath(path);
		storageObject.setSize(minIORepository.getObjectSize(path));
		return storageObject;
	}

	public ByteArrayResource getFile(String path) throws IOException, MinioException, GeneralSecurityException {
		try (InputStream inputStream = minIORepository.getObject(path)) {
			ByteArrayResource byteArrayResource = new ByteArrayResource(inputStream.readAllBytes());
			return byteArrayResource;
		}
	}

	public void removeFile(String path) throws IOException, MinioException, GeneralSecurityException {
		if (!minIORepository.isObjectExist(path)) {
			throw new StorageObjectNotFound(path);
		}
		minIORepository.deleteObject(path);
	}

	public StorageObject moveFile(String currentPath, String newPath) throws IOException, MinioException, GeneralSecurityException {
		if (minIORepository.isObjectExist(newPath)) {
			throw new StorageObjectExist(newPath);
		}
		minIORepository.moveObject(currentPath, newPath);
		return getFileInfo(newPath);
	}
	
	public List<StorageObject> uploadFile(String path, MultipartFile multipartFile) throws IOException, MinioException, GeneralSecurityException {
		String filePath = path + multipartFile.getOriginalFilename();
		if (minIORepository.isObjectExist(filePath)) {
			throw new StorageObjectExist(filePath);
		}
		minIORepository.uploadFile(filePath, multipartFile);
		return List.of(getFileInfo(filePath));
	}
}
