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

import io.minio.errors.MinioException;


@Service
public class ResourceService {
	@Autowired
	private FileService fileService;
	
	@Autowired
	private DirectoryService directoryService;

	@Autowired
	private PathService pathService;

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
	
	public List<StorageObject> uploadObject(String path, List<MultipartFile> files) throws IOException, MinioException, GeneralSecurityException{
		if (files.size() == 1) {
			return fileService.uploadFile(path, files.get(0));
		} else {
			return directoryService.uploadFolder(path, files);
		}
	}
	
	
	public List<StorageObject> searchObjects(String userPath, String query) throws IOException, MinioException, GeneralSecurityException{
		List<StorageObject> result = new ArrayList<>();
		result.addAll(fileService.searchFiles(userPath, query));
		result.addAll(directoryService.searchFolders(userPath, query));
		return result;
	}
	
}
