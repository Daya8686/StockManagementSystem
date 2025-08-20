package com.stockmanagement.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockmanagement.DTO.CreateOrganizationDTO;
import com.stockmanagement.service.OrganizationService;
import com.stockmanagement.util.ApiResponseHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/org_info")
public class OrganizationController {
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Validator validator;
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PostMapping(value="/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseHandler> createOrganization(@RequestPart String createOrganizationDTOstr, @RequestPart("image") MultipartFile image ) throws JsonMappingException, JsonProcessingException{
		CreateOrganizationDTO createOrganizationDTO = objectMapper.readValue(createOrganizationDTOstr, CreateOrganizationDTO.class);

        // Step 2: Validate the DTO
        Set<ConstraintViolation<CreateOrganizationDTO>> violations = validator.validate(createOrganizationDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        
        
		ApiResponseHandler apiResponseHandler = organizationService.createOrganization(createOrganizationDTO, image);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(apiResponseHandler);
	}
	
	@GetMapping("/get")
	public ResponseEntity<ApiResponseHandler> getOrganizationOfUser(){
		ApiResponseHandler organizationInformationOfUser = organizationService.getOrganizationInformationOfUser();
		return ResponseEntity.status(HttpStatus.OK).body(organizationInformationOfUser);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PutMapping(value = "/update-info")
	public ResponseEntity<ApiResponseHandler> updateOrganizationInfo(@Valid @RequestBody CreateOrganizationDTO createOrganizationDTO){
		ApiResponseHandler updateOrganizationInfo = organizationService.updateOrganizationInfo(createOrganizationDTO);
		return ResponseEntity.status(HttpStatus.OK).body(updateOrganizationInfo);
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PutMapping(value="/update-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseHandler> updateOrgImage(@RequestPart("image") MultipartFile image ){
		ApiResponseHandler updateOrganizationImage = organizationService.updateOrganizationImage(image);
		return ResponseEntity.status(HttpStatus.OK).body(updateOrganizationImage);
	}
	
	@DeleteMapping("/delete-org")
	@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
	public ResponseEntity<ApiResponseHandler> deleteOrganization(){
		ApiResponseHandler deleteOrg = organizationService.deleteOrg();
		return ResponseEntity.status(HttpStatus.OK).body(deleteOrg);
	}
	

}
