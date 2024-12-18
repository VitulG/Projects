package com.govt.irctc.validation;

import com.govt.irctc.exceptions.SecurityExceptions.LoginValidationException;
import com.govt.irctc.model.Token;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserSessionValidator {
    private final TokenRepository tokenRepository;

    @Autowired
    public UserSessionValidator(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void validateUserSession(User user) throws LoginValidationException {
        for(Token token : user.getUserTokens()) {
            if (!token.isDeleted() && token.getTokenValidity().before(new Date())) {
                expireSession(token);
            }
        }
        for(Token token : user.getUserTokens()) {
            if(!token.isDeleted()) {
                throw new LoginValidationException("Session is already active");
            }
        }
    }

    private void expireSession(Token token) {
        // update token status to expired
        token.setDeleted(true);
        tokenRepository.save(token);
    }
}
