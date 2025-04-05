package com.zaicev.CloudFileStorage.storage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;
import com.zaicev.CloudFileStorage.storage.models.StorageObject;
import com.zaicev.CloudFileStorage.storage.services.ResourceService;

@RestController
@RequestMapping("/resource")
public class ResourceController {
	@Autowired
	private ResourceService resourceService;
	
	@GetMapping()
	public StorageObject getResourceInfo(String path, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) throws Exception {
		return resourceService.getInfo(path, userDetailsImpl.getId());
	}
}
