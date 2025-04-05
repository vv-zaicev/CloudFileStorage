package com.zaicev.CloudFileStorage.storage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaicev.CloudFileStorage.storage.services.DirectoryService;

@RestController
@RequestMapping("/directory")
public class DirectoryController {
	@Autowired
	private DirectoryService directoryService;
}
