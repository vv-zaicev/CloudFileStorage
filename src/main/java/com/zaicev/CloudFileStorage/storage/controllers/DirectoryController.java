package com.zaicev.CloudFileStorage.storage.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;
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
		return directoryService.getAllFromPath(pathService.getFullPath(path, userDetailsImpl.getId()));
	}

	@PostMapping()
	public StorageObject createFolder(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		return directoryService.createFolder(pathService.getFullPath(path, userDetailsImpl.getId()));
	}
}
