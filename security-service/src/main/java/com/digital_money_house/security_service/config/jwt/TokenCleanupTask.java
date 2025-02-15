package com.digital_money_house.security_service.config.jwt;

import com.digital_money_house.security_service.repository.IInvalidTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TokenCleanupTask {

    private final IInvalidTokenRepository invalidTokenRepository;

    @Autowired
    public TokenCleanupTask(IInvalidTokenRepository invalidTokenRepository) {
        this.invalidTokenRepository = invalidTokenRepository;
    }

    @Scheduled(fixedRate = 3600000) // Ejecutar cada hora
    public void cleanExpiredTokens() {
        invalidTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
