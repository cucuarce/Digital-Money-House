package com.digital_money_house.security_service.repository;

import com.digital_money_house.security_service.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IInvalidTokenRepository extends JpaRepository<InvalidToken, Long> {

    // Buscar si un token ya está invalidado
    Optional<InvalidToken> findByToken(String token);

    // Eliminar tokens que han expirado
    @Modifying
    @Query("DELETE FROM InvalidToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    boolean existsByToken(String token);

}
