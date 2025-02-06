package com.stockmanagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stockmanagement.DTO.AllUsersResponseDTO;
import com.stockmanagement.DTO.ContactDTO;
import com.stockmanagement.DTO.OrganizationDTO;
import com.stockmanagement.DTO.SuperAdminRegistrationDTO;
import com.stockmanagement.DTO.UserLoginDTO;
import com.stockmanagement.DTO.UserRegistrationDTO;
import com.stockmanagement.DTO.UserUpdateDTO;
import com.stockmanagement.DTO.UsersDTO;
import com.stockmanagement.Exception.ContactServiceExceptionHandler;
import com.stockmanagement.Exception.ImageSaverServiceExceptionHandler;
import com.stockmanagement.Exception.UserServiceExceptionHandler;
import com.stockmanagement.controller.OrganizationController;
import com.stockmanagement.controller.UserAuthController;
import com.stockmanagement.entity.Contact;
import com.stockmanagement.entity.Organization;
import com.stockmanagement.entity.PasswordResetToken;
import com.stockmanagement.entity.Role;
import com.stockmanagement.entity.UserVerificationToken;
import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.ContactRepo;
import com.stockmanagement.repository.OrganizationRepo;
import com.stockmanagement.repository.PasswordResetTokenRepostory;
import com.stockmanagement.repository.UserVerificationTokenRepository;
import com.stockmanagement.repository.UsersRepo;
import com.stockmanagement.util.ApiResponseHandler;
import com.stockmanagement.util.JWTTokenResponse;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JWTService jwtService;

	@Autowired
	private UsersRepo usersRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private OrganizationController organizationController;
	
	@Autowired
	private PasswordResetTokenRepostory tokenRepository;
	
	@Autowired
	private UserVerificationTokenRepository userVerificationTokenRepository;
	
	@Autowired
	@Lazy
	private EmailService emailService;
	
	@Value("${app.reset-password-url}")
	String applicationResetPasswordUrl;
	
	@Value("${app.user-verification-url}")
	String userVerificationUrl;
	
	private static final Logger logger = LoggerFactory.getLogger(UserAuthController.class);
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	private final String baseImageDir = "src/main/resources/static/images/users";

	public Object login(UserLoginDTO userLoginDTO) {
		Users checkUser = usersRepo.findByUsername(userLoginDTO.getUsername());
		if (checkUser == null) {
			throw new UserServiceExceptionHandler("User not found with this username " + userLoginDTO.getUsername(),
					HttpStatus.BAD_REQUEST);
		}
		if(!checkUser.isUserValid()) {
	        return verifyEmail(checkUser.getContact().getEmail());	
		}
		if (!checkUser.isUserActive()) {
			throw new UserServiceExceptionHandler(
					"Username with " + userLoginDTO.getUsername() + " is locked!! Please contact application admin",
					HttpStatus.FORBIDDEN);
		}

		try {
		Authentication auth = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
		
//		if (auth.isAuthenticated()) {
			UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
			Users user = userPrincipal.getUser();
			String token = jwtService.generateToken(user);
//            String username2 = jwtService.extractUserName(token);
//            System.out.println(username2);
			JWTTokenResponse jwtTokenResponse = new JWTTokenResponse(token);
			return jwtTokenResponse;
//		}
		}
		catch(BadCredentialsException badCredentialsException){
			
				throw new UserServiceExceptionHandler("Password is invalid!!",HttpStatus.FORBIDDEN );	
		}
		catch(Exception e){
			throw new UserServiceExceptionHandler("Login failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

	private Object verifyEmail(String email) {
		
		String token = UUID.randomUUID().toString();
		
		Optional<Users> userByEmail = usersRepo.findByEmail(email);
		if(userByEmail.isEmpty()) {
			throw new UserServiceExceptionHandler("User email is not present in user table", HttpStatus.NOT_FOUND);
		}
		UserVerificationToken verificationToken = new UserVerificationToken(userByEmail.get(), token);
		userVerificationTokenRepository.save(verificationToken);

		String verificationLink = userVerificationUrl+"/" + token;
		
		try {
			emailService.sendUserVerificationEmail(email, verificationLink, userByEmail.get().getFirstName());
		} catch (MessagingException e) {
			throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ApiResponseHandler("User Verification link has been sent!!", HttpStatus.OK.value(),"Success");
	}

	@Transactional
	@Modifying
	public ApiResponseHandler registerUser(UserRegistrationDTO userRegistrationDTO, MultipartFile image) {
		Users userByUsername = usersRepo.findByUsername(userRegistrationDTO.getUsername());
		if (userByUsername != null) {
			throw new UserServiceExceptionHandler("User with this Username already exists!", HttpStatus.BAD_REQUEST);
		}
		Contact contactByEmail = contactRepo.findByEmail(userRegistrationDTO.getContactDto().getEmail());
		if (contactByEmail != null) {
			throw new ContactServiceExceptionHandler("User with this Email already exists!", HttpStatus.BAD_REQUEST);
		}
		Contact contactByMobile = contactRepo.findByMobile(userRegistrationDTO.getContactDto().getMobile());
		if (contactByMobile != null) {
			throw new ContactServiceExceptionHandler("User with this Mobile already exists!", HttpStatus.BAD_REQUEST);
		}
		if (image != null && !image.isEmpty()) {
		if(image.getContentType()==null || !ImageService.ALLOWED_IMAGE_TYPES.contains(image.getContentType())) {
			throw new ImageSaverServiceExceptionHandler("Invalid image type. Allowed types are PNG, JPG, JPEG, SVG.", HttpStatus.BAD_REQUEST);
		}
		}

		Users user = modelMapper.map(userRegistrationDTO, Users.class);
		ContactDTO contactdto = userRegistrationDTO.getContactDto();
		Contact contact = modelMapper.map(contactdto, Contact.class);
		Contact savedContact = contactRepo.save(contact);
		user.setContact(savedContact);
		user.setRole(Role.SALESMAN);
		user.setUserActive(true);
		user.setUserValid(false);
		Organization organization = organizationRepo.findByOrganizationCode(userRegistrationDTO.getOrganizationCode());
		if (organization == null) {
			throw new UserServiceExceptionHandler("Organization code is invalid", HttpStatus.BAD_REQUEST);
		}
		user.setOrganization(organization);
		user.setPassword(encoder.encode(user.getPassword()));

		// First it will save the user info in DB and then only we get the user ID from
		// there
		user.setImagePath(null);
		// Saving User in DB and then getting user Id for image
		try {
			Users savedUser = usersRepo.save(user);

			// Now calling the method for image path
			if (image != null && !image.isEmpty()) {
				String imagePathForImage = ImageService.getImagePathForImage(image, savedUser.getId(), baseImageDir);
				savedUser.setImagePath("users/" + imagePathForImage);
			}

			Users savedUserFinal = usersRepo.save(savedUser);
			
			verifyEmail(savedUserFinal.getContact().getEmail()); //verification link sent

			AllUsersResponseDTO userResponse = modelMapper.map(savedUserFinal, AllUsersResponseDTO.class);
			userResponse.setContactDto(modelMapper.map(savedUserFinal.getContact(), ContactDTO.class));
			userResponse.setOrganizationDTO(modelMapper.map(savedUserFinal.getOrganization(), OrganizationDTO.class));

			return new ApiResponseHandler(userResponse, HttpStatus.ACCEPTED.value(), "Success!");
		} catch (Exception e) {
			// If an exception occurs, delete the image (if it was saved)
			if (image != null && !image.isEmpty()) {
				ImageService.rollbackImage(image, baseImageDir);
			}
			// Rollback the transaction and throw the exception
			throw new UserServiceExceptionHandler("Failed to create user: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Transactional
	@Modifying
	public ApiResponseHandler registerAdmin(SuperAdminRegistrationDTO superAdminRegistrationDTO,
			MultipartFile userImage) {
		Users userByUsername = usersRepo.findByUsername(superAdminRegistrationDTO.getUsername());
		if (userByUsername != null) {
			throw new UserServiceExceptionHandler("User with this Username already exists!", HttpStatus.BAD_REQUEST);
		}
		Contact contactByEmail = contactRepo.findByEmail(superAdminRegistrationDTO.getContactDto().getEmail());
		if (contactByEmail != null) {
			throw new ContactServiceExceptionHandler("User with this Email already exists!", HttpStatus.BAD_REQUEST);
		}
		Contact contactByMobile = contactRepo.findByMobile(superAdminRegistrationDTO.getContactDto().getMobile());
		if (contactByMobile != null) {
			throw new ContactServiceExceptionHandler("User with this Mobile already exists!", HttpStatus.BAD_REQUEST);
		}

		if (userImage != null && !userImage.isEmpty()) {
		if(userImage.getContentType()==null || !ImageService.ALLOWED_IMAGE_TYPES.contains(userImage.getContentType())) {
			throw new ImageSaverServiceExceptionHandler("Invalid image type. Allowed types are PNG, JPG, JPEG, SVG.", HttpStatus.BAD_REQUEST);
		}
		}
		Users user = modelMapper.map(superAdminRegistrationDTO, Users.class);
		ContactDTO contactdto = superAdminRegistrationDTO.getContactDto();
		Contact contact = modelMapper.map(contactdto, Contact.class);
		Contact savedContact = contactRepo.save(contact);
		user.setContact(savedContact);

//		user.setOrganization(null);
		user.setRole(Role.ADMIN);
		user.setUserActive(true);
		user.setUserValid(false);
		user.setImagePath(null);
		user.setPassword(encoder.encode(user.getPassword()));

		try {
		// Saving User in DB and then getting user Id for image

		Users savedUser = usersRepo.save(user);

		// Now calling the method for image path
		if (userImage != null && !userImage.isEmpty()) {
			String imagePathForImage = ImageService.getImagePathForImage(userImage, savedUser.getId(), baseImageDir);

			savedUser.setImagePath("users/" + imagePathForImage);
		}

		Users savedUserFinal = usersRepo.save(savedUser);
		
		verifyEmail(savedUserFinal.getContact().getEmail()); //verification link sent
		
		
		AllUsersResponseDTO userResponse = modelMapper.map(savedUserFinal, AllUsersResponseDTO.class);
		userResponse.setContactDto(modelMapper.map(savedUserFinal.getContact(), ContactDTO.class));
//		userResponse.setOrganizationDTO(modelMapper.map(savedUserFinal.getOrganization(), OrganizationDTO.class));

		ApiResponseHandler apiResponseHandler = new ApiResponseHandler(userResponse, HttpStatus.ACCEPTED.value(),
				"Success");

		return apiResponseHandler;
		
		} catch (Exception e) {
			// If an exception occurs, delete the image (if it was saved)
			if (userImage != null && !userImage.isEmpty()) {
				ImageService.rollbackImage(userImage, baseImageDir);
			}
			// Rollback the transaction and throw the exception
			throw new UserServiceExceptionHandler("Failed to create user: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	}

	@Transactional
	public ApiResponseHandler forgotPassword(String usernameOrEmailOrPhoneNumber) {
		if(usernameOrEmailOrPhoneNumber==null) {
			throw new UserServiceExceptionHandler("No details entered!!", HttpStatus.BAD_REQUEST);
		}
		Optional<Users> optionalUser = usersRepo.findByUsernameOrEmailOrPhoneNumber(usernameOrEmailOrPhoneNumber);
		
		if(optionalUser.isEmpty()) {
			throw new UserServiceExceptionHandler("Entered invalid details, please check!", HttpStatus.BAD_REQUEST);
					
		}
		
		Users user = optionalUser.get();
		
        String token = UUID.randomUUID().toString();
        
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        tokenRepository.save(passwordResetToken);

        String resetLink = applicationResetPasswordUrl+"/" + token;
        
        try {
			emailService.sendResetPasswordEmail(user.getContact().getEmail(), resetLink, user.getFirstName());
		} catch (MessagingException e) {
			throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}

        return new ApiResponseHandler("Password reset link has been sent!!", HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	@Modifying
	public ApiResponseHandler resetPasswordWithToken(String token, String newPassword) {
		
		Optional<PasswordResetToken> optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty() || optionalToken.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return new ApiResponseHandler("Invalid Link or Timeout", HttpStatus.BAD_REQUEST.value(), "Failed");
        }
       
        Users user = optionalToken.get().getUser();
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));  // Encrypt password
        usersRepo.save(user);
        
       
        return new ApiResponseHandler("Password changed successfully", HttpStatus.OK.value(), "Success");

		
	}
	
	@Transactional
	@Modifying
	public ApiResponseHandler changePassword(String newPassword) {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal)userDetails).getUser();
		user.setPassword(new BCryptPasswordEncoder().encode(newPassword));  // Encrypt password
        usersRepo.save(user);
        return new ApiResponseHandler("Password is changed", HttpStatus.OK.value(), "Success");
	}
	
	

	@Transactional
	@Modifying
	public ApiResponseHandler updateUserInformation(UserUpdateDTO updateDTO) {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal)userDetails).getUser();
		user.setUsername(updateDTO.getUsername());
		user.setFirstName(updateDTO.getFirstName());
		user.setGender(updateDTO.getGender());
		user.setLastName(updateDTO.getLastName());
		ContactDTO contactDto = updateDTO.getContactDto();
		Contact contact = modelMapper.map(contactDto, Contact.class);
		Contact savedContact = contactRepo.save(contact);
		user.setContact(savedContact);
		usersRepo.save(user);
		return new ApiResponseHandler(updateDTO, HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	public ApiResponseHandler getUserInfo() {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users user = ((UserPrincipal)userDetails).getUser();
		Contact contact = user.getContact();
		UsersDTO usersDTO = modelMapper.map(user, UsersDTO.class);
		ContactDTO contactDTO = modelMapper.map(contact, ContactDTO.class);
		String organizationCode = user.getOrganization().getOrganizationCode();
		usersDTO.setOrganizationCode(organizationCode);
		usersDTO.setContactDto(contactDTO);
		return new ApiResponseHandler(usersDTO, HttpStatus.OK.value(), "Success");
		
	}
	
	@Transactional
	public ApiResponseHandler getAllUserOfSpecificOrganization() {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users users = ((UserPrincipal)userDetails).getUser();
		
		Organization organization = users.getOrganization();
		String organizationCode = organization.getOrganizationCode();
		List<Users> usersList = usersRepo.findByOrganization(organization);
		List<UsersDTO> userListDTO = usersList.stream().map(user->{
			UsersDTO usersDTO = modelMapper.map(user, UsersDTO.class);
			
			Contact contact = user.getContact();
			ContactDTO contactDTO = modelMapper.map(contact, ContactDTO.class);
			usersDTO.setContactDto(contactDTO);
			usersDTO.setOrganizationCode(organizationCode);
			System.out.println(usersDTO);
			return usersDTO;
			
			}).collect(Collectors.toList());
		return new ApiResponseHandler(userListDTO, HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	public ApiResponseHandler userVerification(String token) {
		Optional<UserVerificationToken> tokenInfo = userVerificationTokenRepository.findByToken(token);
		if (tokenInfo.isEmpty() || tokenInfo.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return new ApiResponseHandler("Invalid Link or Timeout", HttpStatus.BAD_REQUEST.value(), "Failed");
        }
		Users user = tokenInfo.get().getUser();
		user.setUserValid(true);
		Users savedUser = usersRepo.save(user);
		try {
			emailService.sendUserVerificationSuccessEmail(savedUser.getContact().getEmail(), savedUser.getFirstName());
		} catch (MessagingException e) {
			throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ApiResponseHandler("User is validated successfully!", HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	public ApiResponseHandler findUserByUserId(UUID userId) {
		Optional<Users> userById = Optional.of(usersRepo.findById(userId).orElseThrow(()-> new UserServiceExceptionHandler("User ID is invalid!!", HttpStatus.NOT_FOUND)));
		Users users = userById.get();
		Contact contact = users.getContact();
		ContactDTO contactsDTO = modelMapper.map(contact, ContactDTO.class);
		UsersDTO userDto = modelMapper.map(users, UsersDTO.class);
		userDto.setContactDto(contactsDTO);
		return new ApiResponseHandler(userDto, HttpStatus.OK.value(), "Success");
		
	}

	@Transactional
	public ApiResponseHandler updateEmail(String email) {
		 String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		    Pattern pattern = Pattern.compile(emailRegex);
		    Matcher matcher = pattern.matcher(email);
		    System.out.println(email);

		    // Check if email matches the regex pattern
		    if (!matcher.matches()) {
		        // If email does not match, return a response indicating failure
		        throw new ContactServiceExceptionHandler("Invalid Email type!!", HttpStatus.BAD_REQUEST);
		    }
		    Optional<Users> byEmail = usersRepo.findByEmail(email);
		    
		    if(byEmail.isPresent()) {
		    	throw new UserServiceExceptionHandler("Email Already Exists, Please try different", HttpStatus.BAD_REQUEST);
		    }
		    verifyUpdateEmail(email);
		return new ApiResponseHandler("Verification Email is sent!! ", HttpStatus.OK.value(), "Success");
		
		
	}
	
	private Object verifyUpdateEmail(String email) {
		
		String token = UUID.randomUUID().toString();
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users users = ((UserPrincipal)userDetails).getUser();
		
		UserVerificationToken verificationToken = new UserVerificationToken(users, token);
		userVerificationTokenRepository.save(verificationToken);

		String verificationLink = userVerificationUrl+"/" + token+"/"+email;
		
		try {
			emailService.sendUserVerificationEmail(email, verificationLink, users.getFirstName());
		} catch (MessagingException e) {
			throw new UserServiceExceptionHandler("Something went wrong!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ApiResponseHandler("User Verification link has been sent!!", HttpStatus.OK.value(),"Success");
	}
	@Transactional
	@Modifying
	public ApiResponseHandler updateEmailWithToken(String token, String email) {
		Optional<UserVerificationToken> byToken = userVerificationTokenRepository.findByToken(token);
		if (byToken.isEmpty() || byToken.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            return new ApiResponseHandler("Invalid Link or Timeout", HttpStatus.BAD_REQUEST.value(), "Failed");
        }
		Users user = byToken.get().getUser();
		Contact contact = user.getContact();
		contact.setEmail(email);
		Contact savedContact = contactRepo.save(contact);
		try {
			emailService.sendUserVerificationSuccessEmail(email, user.getFirstName());
		} catch (MessagingException e) {
			throw new UserServiceExceptionHandler("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ApiResponseHandler(savedContact, HttpStatus.OK.value(), "Success");
	}
	


}
