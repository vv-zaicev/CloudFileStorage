package com.zaicev.CloudFileStorage.dto;

import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageObjectDTO {
	private String name;
    private String path;
    private int size;
    private StorageObjectType type;
}
