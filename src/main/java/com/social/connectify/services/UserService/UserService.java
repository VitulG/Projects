package com.social.connectify.services.UserService;

import com.social.connectify.dto.ChangePasswordRequestDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserUpdateDetailsDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PasswordMismatchException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.exceptions.UserUpdationException;

import java.util.List;

public interface UserService {
    UserDetailsResponseDto getUserDetails(String token) throws InvalidTokenException;
    String updateUserDetails(String token, UserUpdateDetailsDto userUpdateDetailsDto) throws InvalidTokenException,
            UserNotFoundException, PasswordMismatchException, UserUpdationException;
    String changeUserPassword(String token, ChangePasswordRequestDto changePasswordRequestDto) throws InvalidTokenException,
            UserNotFoundException, PasswordMismatchException;
    String deleteUserAccount(String token) throws InvalidTokenException;
    List<String> getUserGroups(String token) throws InvalidTokenException;
}
