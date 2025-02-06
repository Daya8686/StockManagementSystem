package com.stockmanagement.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
	
		@Column(nullable = false)
	    private String username;

	    @Column(nullable = false)
	    private String password;

}
