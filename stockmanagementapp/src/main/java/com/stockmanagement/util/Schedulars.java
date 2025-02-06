package com.stockmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.stockmanagement.repository.PasswordResetTokenRepostory;
import com.stockmanagement.repository.UserVerificationTokenRepository;

@Component // Add @Component to make this class a Spring-managed bean
public class Schedulars {

    @Autowired
    private PasswordResetTokenRepostory tokenRepository;
    
    @Autowired
    private UserVerificationTokenRepository verificationTokenRepository;
    

    private static final Logger logger = LoggerFactory.getLogger(Schedulars.class);

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Kolkata") // 30 seconds interval, adjust as needed
    public void deleteExpiredTokens() {
        try {
            logger.info("Scheduled task: Deleting expired tokens...");
            logger.info("Running daily task at 4am IST");
            tokenRepository.deleteExpiredTokens();
            logger.info("Expired tokens deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting expired tokens: ", e);
        }
    }
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Kolkata") // 30 seconds interval, adjust as needed
    public void deleteVerificationUserTokens() {
        try {
            logger.info("Scheduled task: Deleting user verification expired tokens...");
            logger.info("Running daily task at 4am IST");
            verificationTokenRepository.deleteExpiredVerificationTokens();
            logger.info("Expired User Verification tokens deleted successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting user verification expired tokens: ", e);
        }
    }
}
