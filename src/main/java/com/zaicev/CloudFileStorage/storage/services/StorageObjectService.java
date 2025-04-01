package com.zaicev.CloudFileStorage.storage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;
import com.zaicev.CloudFileStorage.storage.repository.MinIORepository;

@Service
public class StorageObjectService {

	@Autowired
	private MinIORepository minIORepository;

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

}
