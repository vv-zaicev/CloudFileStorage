package com.zaicev.CloudFileStorage.storage.exception;

public class StorageObjectNotFound extends StorageException{

	public StorageObjectNotFound(String path) {
		super("%s not found".formatted(path));
	}
	
}
