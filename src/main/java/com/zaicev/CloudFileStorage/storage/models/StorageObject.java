package com.zaicev.CloudFileStorage.storage.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageObject {
	
	private String name;
	
	private String path;
	
	private StorageObjectType type;
}
