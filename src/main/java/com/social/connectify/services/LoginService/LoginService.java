package com.social.connectify.services.LoginService;

import com.social.connectify.dto.UserDetailsDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserLoginDetailsDto;
import com.social.connectify.exceptions.*;

public interface LoginService {
    String signUpUser(UserDetailsDto userDetailsDto) throws UserAlreadyExistsException,
            UserCreationException, PasswordMismatchException;
    String loginUser(UserLoginDetailsDto userLoginDetailsDto) throws UserNotFoundException,
            InvalidCredentialsException, SessionAlreadyActiveException, PasswordMismatchException;
    UserDetailsResponseDto validateToken(String token) throws InvalidTokenException;
    String logoutUser(String token) throws InvalidTokenException;
}
