package com.govt.irctc.validation;

import com.govt.irctc.model.Token;
import com.govt.irctc.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class TokenValidation {
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenValidation(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean isTokenValid(String token) {
        Optional<Token> existingToken = tokenRepository.findByTokenValue(token);

        if (existingToken.isEmpty()) {
            return false;
        }

        Token token1 = existingToken.get();

        return !token1.getTokenValidity().before(new Date()) && !token1.isDeleted();
    }
}
