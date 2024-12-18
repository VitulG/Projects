package com.govt.irctc.service.userService;


import com.govt.irctc.dto.*;
import com.govt.irctc.exceptions.SecurityExceptions.*;
import com.govt.irctc.exceptions.UserExceptions.UserAlreadyExistsException;
import com.govt.irctc.exceptions.UserExceptions.UserCreationException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;

import java.util.List;

public interface UserService {
    public String addUser(UserSignupDetailsDto userSignupDetailsDto)
            throws UserCreationException, UserAlreadyExistsException;
    public String logoutUser(String token) throws TokenNotFoundException;
    public UserDto validateUserToken(String token) throws TokenNotFoundException, InvalidTokenException;
    public LoginResponseDto getAndValidateUser(LoginDetailsDto loginDetailsDto) throws InvalidCredentialsException, PasswordMismatchException;
    public UserDto getUserByEmail(String email, String token) throws UserNotFoundException, InvalidTokenException, UnauthorizedUserException;
    public List<UserDto> getAllUsers(String token) throws InvalidTokenException, UnauthorizedUserException;
    public String updateUserById(String email, UserDto userDto, String token) throws UserNotFoundException, InvalidTokenException, UnauthorizedUserException;
    public String deleteUserById(String email, String token) throws UserNotFoundException, TokenNotFoundException, UnauthorizedUserException, InvalidTokenException;
    public List<BookingDto> getUserBookings(String email, String token) throws UserNotFoundException, InvalidTokenException, UnauthorizedUserException;
}
