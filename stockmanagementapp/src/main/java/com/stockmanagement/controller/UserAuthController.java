package com.stockmanagement.controller;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmanagement.DTO.SuperAdminRegistrationDTO;
import com.stockmanagement.DTO.UserLoginDTO;
import com.stockmanagement.DTO.UserRegistrationDTO;
import com.stockmanagement.DTO.UserUpdateDTO;
import com.stockmanagement.Exception.UserServiceExceptionHandler;
import com.stockmanagement.entity.Users;
import com.stockmanagement.repository.UsersRepo;
import com.stockmanagement.service.UserService;
import com.stockmanagement.util.ApiResponseHandler;
import com.stockmanagement.util.JWTTokenResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class UserAuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;
	
	
	private final Validator validator;
	
	@Autowired
	private UsersRepo usersRepo;
	
	public UserAuthController(Validator validator) {
		this.validator=validator;
	}

	private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);

	@PostMapping(value = "/register/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseHandler> registerUser(
			@Valid @RequestPart("userRegistrationDTO") String userRegistrationDTOStr,
			@RequestPart(name="image", required = false) MultipartFile image) throws JsonMappingException, JsonProcessingException {
			
		UserRegistrationDTO userRegistrationDTO = objectMapper.readValue(userRegistrationDTOStr,
				UserRegistrationDTO.class);
		
		Users userExist = usersRepo.findByUsername(userRegistrationDTO.getUsername());
		if(userExist!=null) {
			throw new UserServiceExceptionHandler("User with username already exits! ", HttpStatus.CONFLICT);
		}
		
		 Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(userRegistrationDTO);
	        if (!violations.isEmpty()) {
	            throw new ConstraintViolationException(violations);
	        }
	        
		ApiResponseHandler registerUser = userService.registerUser(userRegistrationDTO, image);

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(registerUser);
	}
	

	@PostMapping(value = "/register/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseHandler> registerAdmin( @RequestPart("superAdminRegistrationDTOstr") String superAdminRegistrationDTOstr,
			@RequestPart(name="userImage", required = false) MultipartFile userImage)
			throws JsonMappingException, JsonProcessingException {
		System.out.println(superAdminRegistrationDTOstr);
		// Step 1: Deserialize the JSON string into a DTO
        SuperAdminRegistrationDTO superAdminRegistrationDTO = objectMapper.readValue(
                superAdminRegistrationDTOstr, SuperAdminRegistrationDTO.class);
        Users userExist = usersRepo.findByUsername(superAdminRegistrationDTO.getUsername());
		if(userExist!=null) {
			throw new UserServiceExceptionHandler("Admin with username already exits! ", HttpStatus.CONFLICT);
		}
        // Step 2: Validate the DTO
        Set<ConstraintViolation<SuperAdminRegistrationDTO>> violations = validator.validate(superAdminRegistrationDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
		ApiResponseHandler registerUser = userService.registerAdmin(superAdminRegistrationDTO, userImage);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(registerUser);

	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO loginDTO) {
		Object loginResponse = userService.login(loginDTO);
		if (loginResponse instanceof ApiResponseHandler) {
		    ApiResponseHandler response = (ApiResponseHandler) loginResponse;
		    return ResponseEntity.status(HttpStatus.OK).body(response);
		} else if (loginResponse instanceof JWTTokenResponse) {
		    JWTTokenResponse jwtResponse = (JWTTokenResponse) loginResponse;
		    return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
		} else {
		    throw new UserServiceExceptionHandler("Unknown response type", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/forgot-password/{usernameOrEmailOrPhoneNumber}")
	public ResponseEntity<ApiResponseHandler> login(@PathVariable String usernameOrEmailOrPhoneNumber){
		ApiResponseHandler forgotPassword = userService.forgotPassword(usernameOrEmailOrPhoneNumber);
		return ResponseEntity.status(HttpStatus.OK).body(forgotPassword);
	}
	
	 @PutMapping("/reset-password/{token}")
	    public ResponseEntity<ApiResponseHandler> resetPassword(@PathVariable String token, @RequestParam String newPassword) {
	        ApiResponseHandler resetPasswordWithToken = userService.resetPasswordWithToken(token, newPassword);
	       System.out.println(newPassword);
	        if(resetPasswordWithToken.getHttpStatus()==200) {
	        	return ResponseEntity.status(HttpStatus.OK).body(resetPasswordWithToken);
	        }
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resetPasswordWithToken);
	       
	    }
	 
	 @PutMapping("/verification/{token}")
	 public ResponseEntity<ApiResponseHandler> userVerification(@PathVariable String token){
		 ApiResponseHandler userVerification = userService.userVerification(token);
		 return ResponseEntity.status(HttpStatus.OK).body(userVerification);
	 }
	 
	 @PutMapping("/change-password/{newPassword}")
	 public ResponseEntity<ApiResponseHandler> changePassword(@PathVariable String newPassword){
		 ApiResponseHandler changePassword = userService.changePassword(newPassword);
		 return ResponseEntity.status(HttpStatus.OK).body(changePassword);
	 }
	 
	 @PutMapping("/update-user-info")
	 public ResponseEntity<ApiResponseHandler> updateInfo(@RequestBody UserUpdateDTO updateDTO){
		 ApiResponseHandler updateUserInformation = userService.updateUserInformation(updateDTO);
		 return ResponseEntity.status(HttpStatus.OK).body(updateUserInformation);
		 
	 }
	 @GetMapping("/user-info")
	 public ResponseEntity<ApiResponseHandler> getUser(){
		 ApiResponseHandler userInfo = userService.getUserInfo();
		 return ResponseEntity.status(HttpStatus.OK).body(userInfo);
	 }
	
	 @GetMapping("/users")
	 @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	 public ResponseEntity<ApiResponseHandler> getAllUserOfSpecificOrganization(){
		 ApiResponseHandler allUserOfSpecificOrganization = userService.getAllUserOfSpecificOrganization();
		 return ResponseEntity.status(HttpStatus.OK).body(allUserOfSpecificOrganization);
	 }
	 
	 @GetMapping("/users/{userId}")
	 @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	 public ResponseEntity<ApiResponseHandler> getUserByUserId(@PathVariable UUID userId){
		 ApiResponseHandler userByUserId = userService.findUserByUserId(userId);
		 return ResponseEntity.status(HttpStatus.OK).body(userByUserId);
	 }
	 
	 @PutMapping("/updateEmail/{email}")
	 public ResponseEntity<ApiResponseHandler> updateEmail(@PathVariable String email){
		 ApiResponseHandler updateEmail = userService.updateEmail(email);
		 return ResponseEntity.status(HttpStatus.OK).body(updateEmail);
	 }
	 
	 @PutMapping("/verification/{token}/{email}")
	 public ResponseEntity<ApiResponseHandler> verifyAndUpdateEmail(@PathVariable String token, @PathVariable String email){
		 ApiResponseHandler updateEmailWithToken = userService.updateEmailWithToken(token, email);
		 return ResponseEntity.status(HttpStatus.OK).body(updateEmailWithToken);
	 }
	 
	 
	 
	

}
