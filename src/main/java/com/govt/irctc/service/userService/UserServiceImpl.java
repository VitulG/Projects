package com.govt.irctc.service.userService;

import com.govt.irctc.dto.*;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.SecurityExceptions.*;
import com.govt.irctc.exceptions.UserExceptions.UserAlreadyExistsException;
import com.govt.irctc.exceptions.UserExceptions.UserCreationException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.AddressRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.UserRepository;
import com.govt.irctc.validation.TokenValidation;
import com.govt.irctc.validation.UserDetailsValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;
    private final UserDetailsValidation userDetailsValidation;
    private final TokenValidation tokenValidation;
    private final AddressRepository addressRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository, UserDetailsValidation userDetailsValidation, TokenValidation
                                       tokenValidation, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.userDetailsValidation = userDetailsValidation;
        this.tokenValidation = tokenValidation;
        this.addressRepository = addressRepository;
    }

    @Override
    public String addUser(UserSignupDetailsDto userSignupDetailsDto) throws UserCreationException, UserAlreadyExistsException {
        Optional<User> user = userRepository.findByUserEmail(userSignupDetailsDto.getUserEmail());

        if(user.isPresent() && !user.get().isDeleted()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if(!userDetailsValidation.validateUserName(userSignupDetailsDto.getUsername())) {
            throw new UserCreationException("invalid username, username must starts with an alphabet");
        }

        if(!userDetailsValidation.validatePassword(userSignupDetailsDto.getPassword())) {
            throw new UserCreationException("invalid password, password must contain one alphanumeric and special character" +
                    "must required");
        }

        if(!userDetailsValidation.validateEmail(userSignupDetailsDto.getUserEmail())) {
            throw new UserCreationException("invalid email address, for e.g. abc@abc.com");
        }

        if(!userDetailsValidation.validateUserAge(userSignupDetailsDto.getUserAge())) {
            throw new UserCreationException("invalid user age, age must be between 1 and 124");
        }

        if(!userDetailsValidation.validatePhoneNumber(userSignupDetailsDto.getUserPhoneNumber())) {
            throw new UserCreationException("Invalid phone number, phone number must contain only numbers");
        }

        if(!userDetailsValidation.validateUserGender(userSignupDetailsDto.getUserGender())) {
            throw new UserCreationException("Invalid user gender, gender must be one of 'm', 'f'");
        }

        if(!userDetailsValidation.validateUserDateOfBirth(userSignupDetailsDto.getUserDob())) {
            throw new UserCreationException("Invalid date of birth, dob must be past date only");
        }

        if(!userDetailsValidation.validateUserRole(userSignupDetailsDto.getUserRole())) {
            throw new UserCreationException("Invalid User Role. role can either user or admin");
        }

        User newUser = new User();
        newUser.setUserAge(userSignupDetailsDto.getUserAge());
        newUser.setUserDob(userSignupDetailsDto.getUserDob());
        newUser.setUserEmail(userSignupDetailsDto.getUserEmail());
        newUser.setUserName(userSignupDetailsDto.getUsername());
        newUser.setUserGender(userSignupDetailsDto.getUserGender());
        newUser.setHashedPassword(bCryptPasswordEncoder.encode(userSignupDetailsDto.getPassword()));
        newUser.setUserPhoneNumber(userSignupDetailsDto.getUserPhoneNumber());
        newUser.setProfilePictureUrl(userSignupDetailsDto.getProfilePictureUrl());
        newUser.setUserTokens(new ArrayList<>());
        newUser.setUserAddresses(new ArrayList<>());
        newUser.setUserRoles(new ArrayList<>());
        newUser.setUserBookings(new ArrayList<>());

        String givenRole = userSignupDetailsDto.getUserRole();

        if(!givenRole.equalsIgnoreCase("admin") && !givenRole.equalsIgnoreCase("user")) {
            throw new UserCreationException("invalid given user role");
        }

        newUser.getUserRoles().add(UserRole.valueOf(givenRole.toUpperCase()));
        User createdUser = userRepository.save(newUser);

        Address userAddress = new Address();
        userAddress.setHouseNumber(userSignupDetailsDto.getHouseNumber());
        userAddress.setStreet(userSignupDetailsDto.getStreetName());
        userAddress.setCity(userSignupDetailsDto.getCity());
        userAddress.setState(userSignupDetailsDto.getState());
        userAddress.setCountry(userSignupDetailsDto.getCountry());
        userAddress.setPinCode(userSignupDetailsDto.getPinCode());
        userAddress.setUser(newUser);
        addressRepository.save(userAddress);

        newUser.getUserAddresses().add(userAddress);
        return "User created successfully with id: "+ createdUser.getId();
    }

    @Override
    public String logoutUser(String token) throws TokenNotFoundException {
        Optional<Token> getToken = tokenRepository.findByTokenValue(token);

        if(getToken.isEmpty()) {
            throw new TokenNotFoundException("token not found");
        }

        if(tokenValidation.isTokenValid(token)) {
            throw new TokenNotFoundException("Invalid token");
        }

        Token existingToken = getToken.get();

        existingToken.setDeleted(true);
        tokenRepository.save(existingToken);

        return "logged out successfully";
    }

    @Override
    public UserDto validateUserToken(String token) throws TokenNotFoundException, InvalidTokenException {
        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }

        Optional<Token> getToken = tokenRepository.findByTokenValue(token);
        if(getToken.isEmpty()) {
            throw new TokenNotFoundException("token not found");
        }
        Token existingToken = getToken.get();
        return null;
    }

    @Override
    public LoginResponseDto getAndValidateUser(LoginDetailsDto loginDetailsDto) throws InvalidCredentialsException, PasswordMismatchException {
        User user = userRepository.findByUserEmail(loginDetailsDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));

        if(!bCryptPasswordEncoder.matches(loginDetailsDto.getPassword(), user.getHashedPassword())) {
            throw new PasswordMismatchException("Password is not matching");
        }

        // Generate the token
        Token token = generateToken(user);
        tokenRepository.save(token);

        if(user.getUserTokens() == null) {
            user.setUserTokens(new ArrayList<>());
        }

        user.getUserTokens().add(token);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setName(user.getUserName());
        loginResponseDto.setEmail(user.getUserEmail());

        return loginResponseDto;
    }

    private Token generateToken(User user) {
        String token = UUID.randomUUID().toString();
        Token newToken = new Token();

        newToken.setCreatedAt(LocalDateTime.now());
        newToken.setUserTokens(user);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);


        return newToken;
    }

    @Override
    public UserDto getUserByEmail(String email, String token) throws
            UserNotFoundException, InvalidTokenException, UnauthorizedUserException {

        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        Optional<User> user = userRepository.findByUserEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("user not found");
        }

        if(!user.get().getUserRoles().toString().equalsIgnoreCase(UserRole.ADMIN.toString())
                && !user.get().getUserEmail().equals(email)) {
            throw new UnauthorizedUserException("user is not authorized");
        }

        if(user.get().isDeleted()) {
            throw new UserNotFoundException("user doesn't exists");
        }
        return null;
    }

    @Override
    public List<UserDto> getAllUsers(String token) throws InvalidTokenException, UnauthorizedUserException {
        Optional<Token> getToken = tokenRepository.findByTokenValue(token);

        if(getToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        User user = getToken.get().getUserTokens();

        boolean isAdmin = false;

        if(!isAdmin) {
            throw new UnauthorizedUserException("user is not authorized");
        }

        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        for(User u : users) {
            if(!u.isDeleted()) {

            }
        }
        return userDtos;
    }

    @Override
    public String updateUserById(String email, UserDto updatedUser, String token) throws UserNotFoundException,
            InvalidTokenException, UnauthorizedUserException {
        Optional<User> user = userRepository.findByUserEmail(email);

        if(user.isEmpty()) {
            throw new UserNotFoundException("user not exists");
        }

        Optional<Token> getToken = tokenRepository.findByTokenValue(token);

        if(getToken.isEmpty()) {
            throw new InvalidTokenException("token doesn't exists");
        }

        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        User existingUser = user.get();

        if(!getToken.get().getUserTokens().getUserEmail().equals(existingUser.getUserEmail())) {
            throw new UnauthorizedUserException("user is unauthorized");
        }

        existingUser.setUserDob(updatedUser.getUserDob());
        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setUserEmail(updatedUser.getUserEmail());
        existingUser.setUserAge(updatedUser.getUserAge());
//        existingUser.setUserAddress(updatedUser.getUserAddress());
//        existingUser.setUserGender(updatedUser.getUserGender());
        existingUser.setUserPhoneNumber(updatedUser.getUserPhoneNumber());
        existingUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(existingUser);

        return "user updated successfully";
    }

    @Override
    public String deleteUserById(String email, String token) throws UserNotFoundException, TokenNotFoundException,
            UnauthorizedUserException, InvalidTokenException {
        Optional<User> user = userRepository.findByUserEmail(email);

        if(user.isEmpty()) {
            throw new UserNotFoundException("user doesn't exists");
        }

        Optional<Token> getToken = tokenRepository.findByTokenValue(token);
        if(getToken.isEmpty()) {
            throw new TokenNotFoundException("token not found");
        }

        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        User existingUser = user.get();

//        if(!getToken.get().getUserTokens().getUserEmail().equals(existingUser.getUserEmail()) &&
//                getToken.get().getUserTokens().getUserRoles().stream()
//                        .noneMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()))) {
//            throw new UnauthorizedUserException("user is not authorized");
//        }

        // first set their token as deleted
        for(Token tokens : existingUser.getUserTokens()) {
            tokens.setDeleted(true);
        }

        existingUser.setDeleted(true);
        userRepository.save(existingUser);
        return "user deleted successfully";
    }

    @Override
    public List<BookingDto> getUserBookings(String email, String token) throws UserNotFoundException,
            InvalidTokenException, UnauthorizedUserException {

        if(!tokenValidation.isTokenValid(token)) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        Optional<Token> getToken = tokenRepository.findByTokenValue(token);

        if(getToken.isEmpty()) {
            throw new InvalidTokenException("token not found");
        }

        Optional<User> user = userRepository.findByUserEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("User does not exists");
        }

//        if(!email.equals(user.get().getUserEmail()) &&
//                getToken.get().getUserTokens().getUserRoles()
//                        .stream().noneMatch(role -> role.getName().
//                                equalsIgnoreCase(UserRole.ADMIN.toString()))) {
//            throw new UnauthorizedUserException("User is not authorized");
//        }

        List<BookingDto> bookings = new ArrayList<>();

        for(Booking booking : user.get().getUserBookings()) {

        }
        return bookings;
    }
}
