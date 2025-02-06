package com.stockmanagement.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;

public class UserPrincipleObject {
	
	public static Users getUser() {
		UserDetails userDetails =(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Users users = ((UserPrincipal)userDetails).getUser();
		return users;
	}

}
