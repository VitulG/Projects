package com.govt.irctc.service;

import com.govt.irctc.advice.LoginAdvice.InvalidCredentialsException;
import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.LoginAdvice.PasswordMismatchException;
import com.govt.irctc.advice.LoginAdvice.TokenNotFoundException;
import com.govt.irctc.advice.UnauthorizedUserException;
import com.govt.irctc.advice.UserAlreadyExistsException;
import com.govt.irctc.advice.UserCreationException;
import com.govt.irctc.advice.UserNotFoundException;
import com.govt.irctc.dto.*;

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
