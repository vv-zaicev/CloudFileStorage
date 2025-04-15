package com.zaicev.CloudFileStorage.storage.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectExist;
import com.zaicev.CloudFileStorage.storage.exception.StorageObjectNotFound;
import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.models.StorageObjectType;
import com.zaicev.CloudFileStorage.storage.services.PathService;
import com.zaicev.CloudFileStorage.storage.services.ResourceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {
	@Autowired
	private PathService pathService;

	@Autowired
	private ResourceService resourceService;

	@GetMapping()
	public StorageObject getResourceInfo(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		StorageObject storageObject;
		String fullPath = pathService.getFullPath(path, userDetailsImpl.getId());
		try {
			storageObject = resourceService.getObjectInfo(fullPath);
		} catch (StorageObjectNotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + " is not found");
		}
		return storageObject;
	}

	@GetMapping("/download")
	public ResponseEntity<ByteArrayResource> downloadResource(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl)
			throws Exception {
		ByteArrayResource byteArrayResource;
		String fullPath = pathService.getFullPath(path, userDetailsImpl.getId());
		try {
			byteArrayResource = resourceService.getObject(fullPath);
		} catch (StorageObjectNotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + " is not found");
		}

		String fileName = pathService.getResourceName(path) + (pathService.getStorageObjectType(path) == StorageObjectType.DIRECTORY ? ".zip" : "");

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.body(byteArrayResource);
	}

	@GetMapping("/move")
	public StorageObject moveResource(String from, String to, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		StorageObject storageObject;
		String fullFromPath = pathService.getFullPath(from, userDetailsImpl.getId());
		String fullToPath = pathService.getFullPath(to, userDetailsImpl.getId());

		try {
			storageObject = resourceService.moveObject(fullFromPath, fullToPath);
		} catch (StorageObjectNotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, from + " is not found");
		} catch (StorageObjectExist e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, to);
		}

		return storageObject;
	}
	
	@GetMapping("/search")
	public List<StorageObject> searchObjects(String query, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception{
		String userPath = pathService.getFullPath("", userDetailsImpl.getId());
		
		return resourceService.searchObjects(userPath, query);
	}
	
	@PostMapping()
	@ResponseStatus(HttpStatus.CREATED)
	public List<StorageObject> uploadResource(String path, @RequestParam("object") List<MultipartFile> files, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception{
		List<StorageObject> storageObjects = new ArrayList<>();
		try {
			String fullPath = pathService.getFullPath(path, userDetailsImpl.getId());
			storageObjects = resourceService.uploadObject(fullPath, files);
		} catch (StorageObjectExist e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, path);
		}
		
		return storageObjects;
	}

	@DeleteMapping()
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteResource(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		String fullPath = pathService.getFullPath(path, userDetailsImpl.getId());
		try {
			resourceService.removeObject(fullPath);
		} catch (StorageObjectNotFound e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, path + " is not found");
		}
	}
}
