package com.stockmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.stockmanagement.service.ImageService;

@RestController
public class ImageController {
	
	@Value("${image.base-directory}")
	private String baseDirectory;
	
	@Autowired
	private ImageService imageService;
	
	 @GetMapping("/images/{imageNameWithDirectory}")
	public ResponseEntity<byte[]> getImage(@PathVariable String imageNameWithDirectory){
		String fullDirectory=baseDirectory+imageNameWithDirectory;
		ResponseEntity<byte[]> image = imageService.getImage(fullDirectory,imageNameWithDirectory);
		return image;
		
	}

}
