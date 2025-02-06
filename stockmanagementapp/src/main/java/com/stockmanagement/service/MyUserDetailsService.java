package com.stockmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stockmanagement.entity.Users;
import com.stockmanagement.principal.UserPrincipal;
import com.stockmanagement.repository.UsersRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsersRepo usersRepo;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = usersRepo.findByUsername(username);
		if(user!=null) {
			return new UserPrincipal(user);
		}
		throw new UsernameNotFoundException("User not found with username: " + username);
	}

}
