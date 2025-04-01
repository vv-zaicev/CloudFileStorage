package com.zaicev.CloudFileStorage.storage.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.storage.repository.MinIORepository;

@Service
public class FileService {
	@Autowired
	private MinIORepository minIORepository;
		
}
