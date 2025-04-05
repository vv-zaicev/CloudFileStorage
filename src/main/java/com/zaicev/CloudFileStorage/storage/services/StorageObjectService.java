package com.zaicev.CloudFileStorage.storage.services;

import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;

@Service
public class StorageObjectService {

	private final String fullPathPattern = "user-%d-files/%s";

	public StorageObject getStorageObjectFromPath(String path) {
		StorageObject storageObject = new StorageObject();
		if (path.endsWith("/")) {
			storageObject.setType(StorageObjectType.DIRECTORY);
			storageObject.setName(path.substring(path.lastIndexOf('/', path.length() - 2) + 1, path.length() - 1));
			storageObject.setPath(path.substring(0, path.lastIndexOf('/', path.length() - 2) + 1));
		} else {
			storageObject.setType(StorageObjectType.FILE);
			storageObject.setName(path.substring(path.lastIndexOf('/') + 1));
			storageObject.setPath(path.substring(0, path.lastIndexOf('/') + 1));
		}
		return storageObject;
	}

	public String getFullPath(String path, Long userId) {
		return fullPathPattern.formatted(userId, path);
	}

}
