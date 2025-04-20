package com.zaicev.CloudFileStorage.storage.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;
import com.zaicev.CloudFileStorage.storage.repository.MinIORepository;

import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;


@Service
public class ResourceService {
	@Autowired
	private FileService fileService;
	
	@Autowired
	private DirectoryService directoryService;

	@Autowired
	private PathService pathService;
	
	@Autowired
	private MinIORepository minIORepository;

	public StorageObject getObjectInfo(String path) throws IOException, MinioException, GeneralSecurityException {
		if (pathService.getStorageObjectType(path) == StorageObjectType.FILE) {
			return fileService.getFileInfo(path);
		} else {
			return directoryService.getFolderInfo(path);
		}
	}
	
	public ByteArrayResource getObject(String path) throws IOException, MinioException, GeneralSecurityException{
		if (pathService.getStorageObjectType(path) == StorageObjectType.FILE) {
			return fileService.getFile(path);
		} else {
			return directoryService.getFolder(path);
		}
	}

	public void removeObject(String path) throws IOException, MinioException, GeneralSecurityException {
		if (pathService.getStorageObjectType(path) == StorageObjectType.FILE) {
			fileService.removeFile(path);
		} else {
			directoryService.removeFolder(path);
		}
	}
	
	public StorageObject moveObject(String currentPath, String newPath) throws IOException, MinioException, GeneralSecurityException {
		if (pathService.getStorageObjectType(currentPath) == StorageObjectType.FILE) {
			return fileService.moveFile(currentPath, newPath);
		} else {
			return directoryService.moveFolder(currentPath, newPath);
		}
	}
	
	public List<StorageObject> uploadObject(String path, MultipartFile[] files) throws IOException, MinioException, GeneralSecurityException{
		if (files.length == 1) {
			return fileService.uploadFile(path, files[0]);
		} else {
			return directoryService.uploadFolder(path, files);
		}
	}
	
	
	public List<StorageObject> searchObjects(String userPath, String query) throws IOException, MinioException, GeneralSecurityException{
		Iterable<Result<Item>> results = minIORepository.getFiles(userPath, true);
		List<StorageObject> findFiles = new ArrayList<>();
		
		for (Result<Item> result : results) {
			Item item = result.get();
			StorageObject storageObject = pathService.getStorageObjectFromFullPath(item.objectName());
			if (storageObject.getName().toLowerCase().contains(query.toLowerCase())) {
				if(storageObject.getType() == StorageObjectType.FILE) {
					storageObject.setSize(item.size());
				}
				findFiles.add(storageObject);
			}
		}
		
		return findFiles;
	}
	
}
