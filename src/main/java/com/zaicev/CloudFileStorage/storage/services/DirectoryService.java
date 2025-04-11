package com.zaicev.CloudFileStorage.storage.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zaicev.CloudFileStorage.storage.exception.StorageObjectExist;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectNotFound;
import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.repository.MinIORepository;

import io.minio.Result;
import io.minio.SnowballObject;
import io.minio.errors.MinioException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DirectoryService {
	@Autowired
	private MinIORepository minIORepository;

	@Autowired
	private PathService pathService;

	public StorageObject getFolderInfo(String path) throws IOException, MinioException, GeneralSecurityException {
		StorageObject storageObject = pathService.getStorageObjectFromFullPath(path);
		if (!minIORepository.isFolderExists(path)) {
			throw new StorageObjectNotFound(path);
		}
		return storageObject;
	}

	public ByteArrayResource getFolder(String path) throws IOException, MinioException, GeneralSecurityException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Iterable<Result<Item>> items = minIORepository.getFiles(path, true);
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			for (Result<Item> item : items) {
				String objectName = item.get().objectName();

				InputStream inputStream = minIORepository.getObject(objectName);
				ZipEntry entry = new ZipEntry(objectName.substring(path.length()));
				zos.putNextEntry(entry);
				inputStream.transferTo(zos);

				zos.closeEntry();
				inputStream.close();
			}
		}

		return new ByteArrayResource(baos.toByteArray());
	}

	public void removeFolder(String path) throws IOException, MinioException, GeneralSecurityException {
		if (!minIORepository.isFolderExists(path)) {
			throw new StorageObjectNotFound(path);
		}

		Iterable<Result<Item>> items = minIORepository.getFiles(path, true);
		List<DeleteObject> deleteObjects = new ArrayList<>();

		for (Result<Item> item : items) {
			deleteObjects.add(new DeleteObject(item.get().objectName()));
		}

		Iterable<Result<DeleteError>> results = minIORepository.deleteObjects(deleteObjects);
		for (Result<DeleteError> result : results) {
			DeleteError error = result.get();
			log.error("Error deleting object: {}", error.objectName());
		}
	}

	public StorageObject moveFolder(String currentPath, String newPath) throws IOException, MinioException, GeneralSecurityException {
		if (minIORepository.isFolderExists(newPath)) {
			throw new StorageObjectExist(newPath);
		}
		Iterable<Result<Item>> items = minIORepository.getFiles(currentPath, true);
		for (Result<Item> item : items) {
			String sourcePath = item.get().objectName();
			minIORepository.moveObject(sourcePath, sourcePath.replaceAll(currentPath, newPath));
		}
		return getFolderInfo(newPath);
	}

	public List<StorageObject> uploadFolder(String path, List<MultipartFile> files) throws IOException, MinioException, GeneralSecurityException {
		if (minIORepository.isFolderExists(path)) {
			throw new StorageObjectExist(path);
		}

		List<SnowballObject> objects = new ArrayList<>();
		List<StorageObject> result = new ArrayList<>();

		for (MultipartFile file : files) {
			String fullFilePath = path + file.getOriginalFilename();

			StorageObject storageObject = pathService.getStorageObjectFromFullPath(fullFilePath);
			storageObject.setSize(file.getSize());
			result.add(storageObject);

			try (InputStream inputStream = file.getInputStream()) {
				objects.add(new SnowballObject(fullFilePath, new ByteArrayInputStream(inputStream.readAllBytes()), file.getSize(), null));
			} catch (Exception e) {
				log.error("Error while uploading file: {} {}", file.getOriginalFilename(), e);
			}
		}

		minIORepository.uploadFolder(objects);

		return result;
	}

	public HashSet<StorageObject> searchFolders(String userPath, String query) throws IOException, MinioException, GeneralSecurityException {
		Iterable<Result<Item>> results = minIORepository.getFiles(userPath, true);
		HashSet<StorageObject> findFolders = new HashSet<>();

		for (Result<Item> result : results) {
			Item item = result.get();
			String[] pathElements = item.objectName().split("/");
			StringBuilder currentPath = new StringBuilder();
			for (int i = 1; i < pathElements.length - 1; i++) {
				currentPath.append(pathElements[i] + "/");
				if (pathElements[i].toLowerCase().contains(query.toLowerCase())) {
					findFolders.add(pathService.getStorageObjectFromPath(currentPath.toString()));
				}
			}

		}

		return findFolders;

	}

	public List<StorageObject> getAllFromPath(String directoryPath) throws IOException, MinioException, GeneralSecurityException {
		if (!minIORepository.isFolderExists(directoryPath)) {
			throw new StorageObjectNotFound(directoryPath);
		}
		Iterable<Result<Item>> results = minIORepository.getFiles(directoryPath, false);
		List<StorageObject> objects = new ArrayList<>();

		for (Result<Item> result : results) {
			objects.add(pathService.getStorageObjectFromFullPath(result.get().objectName()));
		}

		return objects;
	}

	public StorageObject createFolder(String path) throws IOException, MinioException, GeneralSecurityException {
		if (minIORepository.isFolderExists(path)) {
			throw new StorageObjectExist(path);
		}
		minIORepository.createFolder(path);
		return getFolderInfo(path);
	}
}
