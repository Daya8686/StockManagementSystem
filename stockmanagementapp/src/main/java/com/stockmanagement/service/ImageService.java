package com.stockmanagement.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.transaction.Transactional;

@Service
public class ImageService {
	
	public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
		    "image/png", "image/jpeg", "image/jpg", "image/svg+xml"
		);

	
	public static String getImagePathForImage(MultipartFile image, UUID id, String baseImageDir) {

		Path folderPath = Paths.get(baseImageDir);

		// Make sure the folder exists (create if not)
		if (!Files.exists(folderPath)) {
			try {
				Files.createDirectories(folderPath);
			} catch (IOException e) {
				throw new ImageSaverServiceExceptionHandler("Folder path issue while saving image",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		String originalFilename = image.getOriginalFilename();
		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		String newFilename = id + fileExtension;

		// Save image in the specified directory
		Path imagePath = Paths.get(baseImageDir + File.separator + newFilename);
		try {
			Files.write(imagePath, image.getBytes());
		} catch (IOException e) {
			throw new ImageSaverServiceExceptionHandler("File write issue while saving image",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return newFilename;

	}

	public static void rollbackImage(MultipartFile image, String baseimagedir) {
		 try {
	            String uniqueFileName = image.getOriginalFilename();
	            Path imagePath = Paths.get(baseimagedir+File.separator, uniqueFileName);
	            Files.deleteIfExists(imagePath);
	        } catch (IOException e) {
	            
	            throw new ImageSaverServiceExceptionHandler("Unable to roll back image!!", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
		
	}

	@Transactional
	public ResponseEntity<byte[]> getImage(String fullDirectory, String imageNameWithDirectory) {
		Path imagePath = Paths.get(fullDirectory);
		try {
		if(!Files.exists(imagePath)) {
			throw new ImageSaverServiceExceptionHandler("No image found!!", HttpStatus.NOT_FOUND);
		}
		byte[] imageBytes = Files.readAllBytes(imagePath);
        String mimeType = Files.probeContentType(imagePath);
        
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageNameWithDirectory + "\"")
                .contentType(MediaType.parseMediaType(mimeType != null ? mimeType : "application/octet-stream"))
                .cacheControl(CacheControl.noCache()) 
                .body(imageBytes);

    } catch (IOException e) {
        throw new ImageSaverServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
		
	}

}
