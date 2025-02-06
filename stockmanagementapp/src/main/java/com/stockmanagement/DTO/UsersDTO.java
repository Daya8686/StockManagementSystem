package com.stockmanagement.DTO;

import java.util.UUID;

import com.stockmanagement.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {
	
	private UUID id;
	
	private String username;

	private String firstName;

	private String lastName;


	private Role role; // MANAGER, SALESMAN, SUPER_ADMIN

	private ContactDTO contactDto;

	private String organizationCode;

	private String gender;

	private String imagePath;

}
