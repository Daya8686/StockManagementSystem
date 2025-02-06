//package com.stockmanagement.util;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
//
//import jakarta.transaction.Transactional;
//
//public class ImageService {
//	
//	public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
//		    "image/png", "image/jpeg", "image/jpg", "image/svg+xml"
//		);
//
//	@Transactional
//	public static String getImagePathForImage(MultipartFile image, UUID id, String baseImageDir) {
//
//		Path folderPath = Paths.get(baseImageDir);
//
//		// Make sure the folder exists (create if not)
//		if (!Files.exists(folderPath)) {
//			try {
//				Files.createDirectories(folderPath);
//			} catch (IOException e) {
//				throw new ImageSaverServiceExceptionHandler("Folder path issue while saving image",
//						HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		}
//		String originalFilename = image.getOriginalFilename();
//		String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//		String newFilename = id + fileExtension;
//
//		// Save image in the specified directory
//		Path imagePath = Paths.get(baseImageDir + File.separator + newFilename);
//		try {
//			Files.write(imagePath, image.getBytes());
//		} catch (IOException e) {
//			throw new ImageSaverServiceExceptionHandler("File write issue while saving image",
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return newFilename;
//
//	}
//
//	public static void rollbackImage(MultipartFile image, String baseimagedir) {
//		 try {
//	            String uniqueFileName = image.getOriginalFilename();
//	            Path imagePath = Paths.get(baseimagedir+File.separator, uniqueFileName);
//	            Files.deleteIfExists(imagePath);
//	        } catch (IOException e) {
//	            
//	            throw new ImageSaverServiceExceptionHandler("Unable to roll back image!!", HttpStatus.INTERNAL_SERVER_ERROR);
//	        }
//		
//	}
//
//}
