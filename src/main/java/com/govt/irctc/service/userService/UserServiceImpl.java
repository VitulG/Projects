package com.govt.irctc.service.userService;

import com.govt.irctc.dto.*;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.SecurityExceptions.*;
import com.govt.irctc.exceptions.UserExceptions.UserAlreadyExistsException;
import com.govt.irctc.exceptions.UserExceptions.UserCreationException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;
import com.govt.irctc.exceptions.UserExceptions.UserUpdationException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.AddressRepository;
import com.govt.irctc.repository.BookingRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.UserRepository;
import com.govt.irctc.validation.UserDetailsValidator;
import com.govt.irctc.validation.UserSessionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;
    private final UserDetailsValidator userDetailsValidator;
    private final AddressRepository addressRepository;
    private final UserSessionValidator userSessionValidator;
    private final BookingRepository bookingRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository, UserDetailsValidator userDetailsValidator, AddressRepository addressRepository,
                           UserSessionValidator userSessionValidator, BookingRepository bookingRepository, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.userDetailsValidator = userDetailsValidator;
        this.addressRepository = addressRepository;
        this.userSessionValidator = userSessionValidator;
        this.bookingRepository = bookingRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String addUser(UserSignupDetailsDto userSignupDetailsDto) throws UserCreationException, UserAlreadyExistsException {
        Optional<User> user = userRepository.findByUserEmail(userSignupDetailsDto.getUserEmail());

        if(user.isPresent() && !user.get().isDeleted()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if(!userDetailsValidator.isValidUserName(userSignupDetailsDto.getUsername())) {
            throw new UserCreationException("invalid username, username must starts with an alphabet");
        }

        if(!userDetailsValidator.isValidPassword(userSignupDetailsDto.getPassword())) {
            throw new UserCreationException("invalid password, password must contain one alphanumeric and special character" +
                    "must required");
        }

        if(!userDetailsValidator.isValidEmail(userSignupDetailsDto.getUserEmail())) {
            throw new UserCreationException("invalid email address, for e.g. abc@abc.com");
        }

        if(!userDetailsValidator.isValidUserAge(userSignupDetailsDto.getUserAge())) {
            throw new UserCreationException("invalid user age, age must be between 1 and 124");
        }

        if(!userDetailsValidator.isValidPhoneNumber(userSignupDetailsDto.getUserPhoneNumber())) {
            throw new UserCreationException("Invalid phone number, phone number must contain only numbers");
        }

        if(!userDetailsValidator.isValidUserGender(userSignupDetailsDto.getUserGender())) {
            throw new UserCreationException("Invalid user gender, gender must be one of 'm', 'f'");
        }

        if(!userDetailsValidator.isValidUserDateOfBirth(userSignupDetailsDto.getUserDob())) {
            throw new UserCreationException("Invalid date of birth, dob must be past date only");
        }

        if(!userDetailsValidator.isValidUserRole(userSignupDetailsDto.getUserRole())) {
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
        newUser.setUserRole(UserRole.valueOf(userSignupDetailsDto.getUserRole().toUpperCase()));
        newUser.setUserBookings(new ArrayList<>());

        String givenRole = userSignupDetailsDto.getUserRole();

        if(!givenRole.equalsIgnoreCase("admin") && !givenRole.equalsIgnoreCase("user")) {
            throw new UserCreationException("invalid given user role");
        }

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
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted()) {
            throw new TokenNotFoundException("Token is already deleted");
        }

        existingToken.setDeleted(true);

        User user = existingToken.getUserTokens();
        user.getUserTokens().remove(existingToken);
        userRepository.save(user);

        tokenRepository.save(existingToken);

        return "logged out successfully";
    }

    @Override
    public UserDto validateUserToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Token is either invalid or expired");
        }
        User user = existingToken.getUserTokens();

        return buildUserDto(user);
    }

    private UserDto buildUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserName(user.getUserName());
        userDto.setUserEmail(user.getUserEmail());
        userDto.setUserDob(user.getUserDob());
        userDto.setUserAge(user.getUserAge());
        userDto.setUserGender(user.getUserGender());
        userDto.setUserPhoneNumber(user.getUserPhoneNumber());
        userDto.setProfilePictureUrl(user.getProfilePictureUrl());

        userDto.setUserBookings(user.getUserBookings().stream()
                .map(Booking::convertToBookingDto)
                .collect(Collectors.toList()));

        userDto.setUserAddresses(user.getUserAddresses().stream()
                .map(Address::convertToAddressDto)
                .collect(Collectors.toList()));

        return userDto;
    }

    @Override
    public LoginResponseDto getAndValidateUser(LoginDetailsDto loginDetailsDto) throws InvalidCredentialsException,
            PasswordMismatchException, LoginValidationException {

        if(redisTemplate.opsForValue().get(loginDetailsDto.getEmail()) != null) {
            return (LoginResponseDto) redisTemplate.opsForValue().get(loginDetailsDto.getEmail());
        }

        User user = userRepository.findByUserEmail(loginDetailsDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("user not found"));

        if(!user.getUserName().equals(loginDetailsDto.getUsername())) {
            throw new LoginValidationException("Invalid username");
        }

        if(!bCryptPasswordEncoder.matches(loginDetailsDto.getPassword(), user.getHashedPassword())) {
            throw new PasswordMismatchException("Password is not matching");
        }

        userSessionValidator.validateUserSession(user);

        // Generate the token
        Token token = generateToken(user);
        tokenRepository.save(token);
        user.getUserTokens().add(token);

        LoginResponseDto response = buildResponse(user);
        redisTemplate.opsForValue().set(loginDetailsDto.getEmail(), response, 1, TimeUnit.HOURS);
        return response;
    }

    private LoginResponseDto buildResponse(User user) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setName(user.getUserName());
        loginResponseDto.setEmail(user.getUserEmail());
        loginResponseDto.setLoggedIn(true);

        return loginResponseDto;
    }

    private Token generateToken(User user) {
        Token newToken = new Token();
        newToken.setUserTokens(user);
        newToken.setTokenValue(UUID.randomUUID().toString());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        newToken.setTokenValidity(calendar.getTime());

        return newToken;
    }

    @Override
    public UserDto getUserByEmail(String email, String token) throws
            UserNotFoundException, InvalidTokenException, UnauthorizedUserException {
        Token existingToken = getToken(token);

        if(redisTemplate.opsForValue().get(email) != null) {
            return (UserDto) redisTemplate.opsForValue().get(email);
        }

        User currentUser = existingToken.getUserTokens();

        if(currentUser.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("user is not authorized");
        }

        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserDto response =  buildUserDto(user);
        redisTemplate.opsForValue().set(email, response, 1, TimeUnit.HOURS);
        return response;
    }

    @Override
    public List<UserDto> getAllUsers(String token) throws InvalidTokenException, UnauthorizedUserException {
        Token existingToken = getToken(token);
        User currentUser = existingToken.getUserTokens();

        if(currentUser.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("user is not authorized");
        }

        if(redisTemplate.opsForValue().get(currentUser.getUserEmail()) != null) {
            return (List<UserDto>) redisTemplate.opsForValue().get(currentUser.getUserEmail());
        }

        List<User> users = userRepository.findAll();
        List<UserDto> allUsers = users.stream()
                .map(this::buildUserDto)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(currentUser.getUserEmail(), allUsers);
        return allUsers;
    }

    private Token getToken(String token) throws InvalidTokenException{
        return tokenRepository.findByTokenValue(token)
               .orElseThrow(() -> new InvalidTokenException("Token not found"));
    }

    @Override
    public String updateUserById(String token, UserUpdateDetailsDto updateDetailsDto) throws
            InvalidTokenException, UnauthorizedUserException, UserUpdationException {
        Token existingToken = getToken(token);

        User currentUser = existingToken.getUserTokens();

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new UnauthorizedUserException("Either token is deleted or expired. please login again to update");
        }

        updateUserDetails(currentUser, updateDetailsDto);
        userRepository.save(currentUser);

        return "user details updated successfully";
    }

    private void updateUserDetails(User user, UserUpdateDetailsDto updateDetailsDto) throws UserUpdationException {
        String prevEmail = user.getUserEmail();

        if (updateDetailsDto.getUpdatedUserName() != null &&
                !userDetailsValidator.isValidUserName(updateDetailsDto.getUpdatedUserName())) {
            throw new UserUpdationException("Incorrect user name");
        }
        user.setUserName(updateDetailsDto.getUpdatedUserName());

        if (updateDetailsDto.getUpdatedAge() > 0 &&
                !userDetailsValidator.isValidUserAge(updateDetailsDto.getUpdatedAge())) {
            throw new UserUpdationException("Invalid user age");
        }
        user.setUserAge(updateDetailsDto.getUpdatedAge());

        if (updateDetailsDto.getUpdatedEmail() != null &&
                !userDetailsValidator.isValidEmail(updateDetailsDto.getUpdatedEmail())) {
            throw new UserUpdationException("Invalid user email");
        }
        user.setUserEmail(updateDetailsDto.getUpdatedEmail());

        if (updateDetailsDto.getUpdatedGender() != null &&
                !userDetailsValidator.isValidUserGender(updateDetailsDto.getUpdatedGender())) {
            throw new UserUpdationException("Invalid user gender");
        }
        user.setUserGender(updateDetailsDto.getUpdatedGender());

        if (updateDetailsDto.getUserDob() != null &&
                !userDetailsValidator.isValidUserDateOfBirth(updateDetailsDto.getUserDob())) {
            throw new UserUpdationException("Invalid user date of birth");
        }
        user.setUserDob(updateDetailsDto.getUserDob());

        if (updateDetailsDto.getUpdatedPhoneNumber() != null &&
                !userDetailsValidator.isValidPhoneNumber(updateDetailsDto.getUpdatedPhoneNumber())) {
            throw new UserUpdationException("Invalid user phone number");
        }
        user.setUserPhoneNumber(updateDetailsDto.getUpdatedPhoneNumber());
        user.setProfilePictureUrl(updateDetailsDto.getProfilePictureUrl());

        if(redisTemplate.opsForValue().get(prevEmail) != null) {
            redisTemplate.delete(prevEmail);
            redisTemplate.opsForValue().set(user.getUserEmail(), user);
        }
    }

    @Override
    public String deleteUserById(String email, String token) throws TokenNotFoundException, UnauthorizedUserException,
            InvalidTokenException {
        validateUserToken(token);

        Token existingToken = getToken(token);
        User existingUser = existingToken.getUserTokens();

        if(!existingUser.getUserEmail().equalsIgnoreCase(email)) {
            throw new UnauthorizedUserException("Invalid user email");
        }

        addressRepository.deleteUserAddresses(existingUser.getId());
        bookingRepository.deleteBookingByUserId(existingUser.getId());
        tokenRepository.updateUserTokens(existingUser.getId());

        if(redisTemplate.opsForValue().get(existingUser.getUserEmail()) != null) {
            redisTemplate.delete(existingUser.getUserEmail());
        }

        userRepository.deleteUserByUserEmail(email);

        return "user deleted successfully";
    }

    @Override
    public List<BookingDto> getUserBookings(String email, String token) throws
            InvalidTokenException, UnauthorizedUserException, TokenNotFoundException {
        validateUserToken(token);

        Token existingToken = getToken(token);
        User currentUser = existingToken.getUserTokens();

        if(!currentUser.getUserEmail().equalsIgnoreCase(email)) {
            throw new UnauthorizedUserException("user is not authorized");
        }

        if(redisTemplate.opsForValue().get(currentUser.getUserEmail()) != null) {
            return (List<BookingDto>) redisTemplate.opsForValue().get(currentUser.getUserEmail());
        }

        List<Booking> bookings = bookingRepository.findAllByUserBookings(currentUser);

        List<BookingDto> userBookings =  bookings.stream()
                .map(Booking::convertToBookingDto)
                .toList();

        redisTemplate.opsForValue().set(currentUser.getUserEmail(), userBookings);
        return userBookings;
    }
}
