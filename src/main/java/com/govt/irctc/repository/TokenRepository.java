package com.govt.irctc.repository;

import com.govt.irctc.model.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String token);

    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.isDeleted = true WHERE t.userTokens.id = :userId")
    void updateUserTokens(@Param("userId") Long userId);

}
