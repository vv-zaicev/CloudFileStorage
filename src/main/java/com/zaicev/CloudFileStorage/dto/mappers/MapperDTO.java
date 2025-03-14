package com.zaicev.CloudFileStorage.dto.mappers;

public interface MapperDTO <T, K>{
	public K getObject(T dto);
	public T toDTO(K object);
}
