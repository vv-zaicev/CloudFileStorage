package com.zaicev.CloudFileStorage.storage.exception;

public class StorageObjectExist extends StorageException{

	public StorageObjectExist(String path) {
		super("Object exists: %s".formatted(path));
	}
	
}
