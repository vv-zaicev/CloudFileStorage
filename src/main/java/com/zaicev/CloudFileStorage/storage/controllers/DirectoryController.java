package com.zaicev.CloudFileStorage.storage.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectExist;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectNotFound;
import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.services.DirectoryService;
import com.zaicev.CloudFileStorage.storage.services.PathService;

@RestController
@RequestMapping("/directory")
public class DirectoryController {
	@Autowired
	private DirectoryService directoryService;

	@Autowired
	private PathService pathService;

	@GetMapping()
	public List<StorageObject> getAllFromDirectory(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl)
			throws Exception {
		List<StorageObject> storageObjects = new ArrayList<>();
		try {
			 storageObjects = directoryService.getAllFromPath(pathService.getFullPath(path, userDetailsImpl.getId()));
		} catch (StorageObjectNotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, path);
		}
		
		return storageObjects;
		
	}

	@PostMapping()
	public StorageObject createFolder(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		StorageObject storageObject;
		try {
			storageObject = directoryService.createFolder(pathService.getFullPath(path, userDetailsImpl.getId()))
		} catch (StorageObjectExist e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, path);
		}
		return storageObject;
	}
}
