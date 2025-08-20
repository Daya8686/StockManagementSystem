package com.stockmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stockmanagement.entity.UserVerificationToken;
import com.stockmanagement.entity.Users;

import jakarta.transaction.Transactional;
@Repository
public interface UserVerificationTokenRepository extends JpaRepository<UserVerificationToken, Long>{
	
	Optional<UserVerificationToken> findByToken(String token);
	
	@Transactional
    @Modifying
    @Query(value = "CALL deleteExpiredUserVerificationTokens()", nativeQuery = true)
    void deleteExpiredVerificationTokens();
	
	public UserVerificationToken findByUser(Users user);

}
