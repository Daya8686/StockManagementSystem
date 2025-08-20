package com.stockmanagement.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stockmanagement.DTO.CreateOrganizationDTO;
import com.stockmanagement.DTO.OrganizationDTO;
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.Exception.OrganizationServiceExceptionHandler;
import com.stockmanagement.Exception.UserServiceExceptionHandler;
import com.stockmanagement.entity.Organization;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.OrganizationRepo;
import com.stockmanagement.repository.UsersRepo;
import com.stockmanagement.util.ApiResponseHandler;
import com.stockmanagement.util.UserPrincipleObject;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class OrganizationService {
	
	@Autowired
	private OrganizationRepo organizationRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UsersRepo usersRepo;
	
	
	// Define the length of the organization code
    private static final int ORGANIZATION_CODE_LENGTH = 6;

    // Character set for the organization code (Uppercase letters and digits)
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random = new SecureRandom();
    
    private static final Logger logger = Logger.getLogger(OrganizationService.class.getName());
    
    private final static String baseImageDir = "src/main/resources/static/images/organizations";
	
	@Transactional
	@Modifying
	public ApiResponseHandler createOrganization(CreateOrganizationDTO organizationDTO, MultipartFile image) {
		Organization organization = modelMapper.map(organizationDTO, Organization.class);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Users userForOrgCheck = ((UserPrincipal) userDetails).getUser();
		
		if(userForOrgCheck.getOrganization()!=null) {
			throw new OrganizationServiceExceptionHandler("You are been added with Organization you can not add another organization", HttpStatus.CONFLICT);
		}		

		organization.setOrganizationCode(generateUniqueOrganizationCode());
		try {
		Organization savedOrganization = organizationRepo.save(organization);
		
		if(savedOrganization==null) {
			throw new OrganizationServiceExceptionHandler("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		String newFilename = ImageService.getImagePathForImage(image, savedOrganization.getOrgId(), baseImageDir);
		
		savedOrganization.setImagePath("organizations/"+newFilename);
		
		Organization savedOrgfinal = organizationRepo.save(savedOrganization);
		
		
		
		//Saving org in user
		 UUID userId = ((UserPrincipal) userDetails).getUser().getId();
		Optional<Users> user = usersRepo.findById(userId);
		if(user.isEmpty()) {
			throw new UserServiceExceptionHandler("User not found with this ID", HttpStatus.BAD_REQUEST);
		}
		Users users = user.get();
		users.setOrganization(savedOrgfinal);
		usersRepo.save(users);
		OrganizationDTO orgDto = modelMapper.map(savedOrgfinal, OrganizationDTO.class);
		
		ApiResponseHandler apiResponseHandler= new ApiResponseHandler();
		apiResponseHandler.setData(orgDto);
		apiResponseHandler.setHttpStatus(HttpStatus.ACCEPTED.value());
		apiResponseHandler.setMessage("Success");
		return apiResponseHandler;
		} catch (Exception e) {
			// If an exception occurs, delete the image (if it was saved)
			if (image != null && !image.isEmpty()) {
				ImageService.rollbackImage(image, baseImageDir);
			}
			// Rollback the transaction and throw the exception
			throw new OrganizationServiceExceptionHandler("Failed to create organization: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

		@Transactional
		public ApiResponseHandler getOrganizationInformationOfUser() {
			Users users = UserPrincipleObject.getUser();
			Organization organization = users.getOrganization();
//			if(organization==null) {
//				throw new OrganizationServiceExceptionHandler("You are not associated with any organization", HttpStatus.BAD_REQUEST);
//			}
			OrganizationDTO orgDto = modelMapper.map(organization, OrganizationDTO.class);
			return new ApiResponseHandler(orgDto, HttpStatus.OK.value(), "Success");
			
			
		}
		

		@Transactional
		@Modifying
		public ApiResponseHandler updateOrganizationInfo(CreateOrganizationDTO createOrganizationDTO) {
			Users users = UserPrincipleObject.getUser();
			Organization organization = users.getOrganization();
			organization.setName(createOrganizationDTO.getName());
			organization.setOrganizationOwnerName(createOrganizationDTO.getOrganizationOwnerName());
			organization.setAddress(createOrganizationDTO.getAddress());
			organization.setPincode(createOrganizationDTO.getPincode());
			Organization savedOrganization = organizationRepo.save(organization);
			OrganizationDTO organizationDTO = modelMapper.map(savedOrganization, OrganizationDTO.class);
			
			return new ApiResponseHandler(organizationDTO, HttpStatus.OK.value(), "Success");	
		}
		
		
		@Transactional
		public ApiResponseHandler updateOrganizationImage(MultipartFile image) {
			
			
			Users users = UserPrincipleObject.getUser();
			Organization organization = users.getOrganization();
			
			try {
				
			Path imagePath=Paths.get("src/main/resources/static/images"+File.separator+""+organization.getImagePath());
			Files.deleteIfExists(imagePath);
			String newFilename = ImageService.getImagePathForImage(image, organization.getOrgId(), baseImageDir);
			
			
			organization.setImagePath("organizations/"+newFilename);
			
			Organization savedOrgfinal = organizationRepo.save(organization);
			
			OrganizationDTO organizationDTO = modelMapper.map(savedOrgfinal, OrganizationDTO.class);
			
			return new ApiResponseHandler(organizationDTO, HttpStatus.OK.value(), "Success");
			
		} catch (Exception e) {
			// If an exception occurs, delete the image (if it was saved)
			if (image != null && !image.isEmpty()) {
				ImageService.rollbackImage(image, baseImageDir);
			}
			// Rollback the transaction and throw the exception
			throw new OrganizationServiceExceptionHandler("Failed to change organization image: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
			
		}
		@Transactional
		@Modifying
		public ApiResponseHandler deleteOrg() {
			Users user = UserPrincipleObject.getUser();
			Organization organization = user.getOrganization();
			organizationRepo.delete(organization);
			return new ApiResponseHandler(organization, HttpStatus.OK.value(), "Success");
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		 private String generateUniqueOrganizationCode() {
		        String code = "";
		        boolean isUnique = false;

		        while (!isUnique) {
		            // Generate a new random code
		            code = generateRandomCode();

		            // Check if this code already exists in the database
		            isUnique = organizationRepo.existsByOrganizationCode(code);
		            
		            isUnique=isUnique?false:true;

		            if (!isUnique) {
		                logger.info("Generated code " + code + " already exists. Retrying...");
		            }
		        }

		        logger.info("Generated unique organization code: " + code);
		        return code;
		    }

		    /**
		     * Generates a random 6-character alphanumeric code with a mix of digits and letters.
		     */
		    private String generateRandomCode() {
		        List<Character> codeChars = new ArrayList<>(ORGANIZATION_CODE_LENGTH);

		        // Generate 6 random characters from the charset (both digits and letters)
		        for (int i = 0; i < ORGANIZATION_CODE_LENGTH; i++) {
		            int randomIndex = random.nextInt(CHARSET.length());
		            codeChars.add(CHARSET.charAt(randomIndex));
		        }

		        // Shuffle the list to make sure the characters are randomly ordered
		        Collections.shuffle(codeChars, random);

		        // Convert list back to string
		        StringBuilder code = new StringBuilder();
		        for (Character c : codeChars) {
		            code.append(c);
		        }

		        return code.toString();
		    }


			


}
