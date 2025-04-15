package com.zaicev.CloudFileStorage.storage.services;

import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;

@Service
public class PathService {
	
	private final String fullPathPattern = "user-%d-files/%s";
	
	public StorageObject getStorageObjectFromFullPath(String fullPath) {
		return getStorageObjectFromPath(fullPath.substring(fullPath.indexOf('/') + 1));
	}

	public StorageObject getStorageObjectFromPath(String path) {
		StorageObject storageObject = new StorageObject();
		storageObject.setType(getStorageObjectType(path));
		storageObject.setName(getResourceName(path));
		storageObject.setPath(getResourcePathWitouthResourceName(path));
		return storageObject;
	}

	public String getResourceName(String path) {
		if (path.endsWith("/")) {
			return path.substring(path.lastIndexOf('/', path.length() - 2) + 1, path.length());
		} else {
			return path.substring(path.lastIndexOf('/') + 1);
		}
	}

	public StorageObjectType getStorageObjectType(String path) {
		if (path.endsWith("/")) {
			return StorageObjectType.DIRECTORY;
		} else {
			return StorageObjectType.FILE;
		}
	}

	public String getResourcePathWitouthResourceName(String path) {
		if (path.endsWith("/")) {
			return path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1);
		} else {
			return path.substring(0, path.lastIndexOf('/') + 1);
		}
	}

	public String getFullPath(String path, Long userId) {
		return fullPathPattern.formatted(userId, path);
	}
}
