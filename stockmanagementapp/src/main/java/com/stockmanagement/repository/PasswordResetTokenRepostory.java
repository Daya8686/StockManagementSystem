package com.stockmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.stockmanagement.entity.PasswordResetToken;

import jakarta.transaction.Transactional;
@Repository
public interface PasswordResetTokenRepostory extends JpaRepository<PasswordResetToken, Long>{
	
	
	    Optional<PasswordResetToken> findByToken(String token);
	    
		@Transactional
	    @Modifying
	    @Query(value = "CALL deleteExpiredPasswordResetTokens()", nativeQuery = true)
	    void deleteExpiredTokens();


}
