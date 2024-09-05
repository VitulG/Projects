package com.social.connectify.services.LoginService;

import com.social.connectify.dto.UserDetailsDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserLoginDetailsDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.Token;
import com.social.connectify.models.User;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import com.social.connectify.validations.UserLoginCredentialsValidator;
import com.social.connectify.validations.UserSignupDetailsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserSignupDetailsValidator userSignupDetailsValidator;
    private final TokenRepository tokenRepository;
    private final UserLoginCredentialsValidator userLoginCredentialsValidator;

    @Autowired
    public LoginServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                           UserSignupDetailsValidator userSignupDetailsValidator, TokenRepository tokenRepository,
                           UserLoginCredentialsValidator userLoginCredentialsValidator) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder  = bCryptPasswordEncoder;
        this.userSignupDetailsValidator = userSignupDetailsValidator;
        this.tokenRepository = tokenRepository;
        this.userLoginCredentialsValidator = userLoginCredentialsValidator;
    }

    @Override
    public String signUpUser(UserDetailsDto userDetailsDto) throws UserAlreadyExistsException,
            UserCreationException, PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findUserByEmail(userDetailsDto.getEmail());

        if(optionalUser.isPresent() && !optionalUser.get().isDeleted()) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            if(!bCryptPasswordEncoder.matches(userDetailsDto.getPassword(), user.getPassword())) {
                throw new PasswordMismatchException("password does not match");
            }

            user.setDeleted(false);
            userRepository.save(user);
            return "user re-activated successfully";
        }

        userSignupDetailsValidator.validateUserSignupDetails(userDetailsDto);

        User user = new User();
        user.setEmail(userDetailsDto.getEmail());
        user.setGender(userDetailsDto.getGender());
        user.setDateOfBirth(userDetailsDto.getDateOfBirth());
        user.setFirstName(userDetailsDto.getFirstName());
        user.setLastName(userDetailsDto.getLastName());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(bCryptPasswordEncoder.encode(userDetailsDto.getPassword()));

        userRepository.save(user);
        return "user account created successfully";
    }

    @Override
    public String loginUser(UserLoginDetailsDto userLoginDetailsDto) throws UserNotFoundException,
            InvalidCredentialsException, SessionAlreadyActiveException, PasswordMismatchException {
        Optional<User> optionalUser = userRepository.findUserByEmail(userLoginDetailsDto.getEmail());

        if(optionalUser.isEmpty() || optionalUser.get().isDeleted()) {
            throw new UserNotFoundException("User does not exists");
        }

        userLoginCredentialsValidator.validateUserLoginDetails(userLoginDetailsDto);
        User user = optionalUser.get();

        if(!bCryptPasswordEncoder.matches(userLoginDetailsDto.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("invalid password");
        }

        for(Token token : user.getUserTokens()) {
            if(token.isActive()) {
                throw new SessionAlreadyActiveException("user is already active");
            }
        }

        // generate token now
        Token token = generateToken(user);
        tokenRepository.save(token);

        if(user.getUserTokens() == null) {
            user.setUserTokens(new ArrayList<>());
        }
        user.getUserTokens().add(token);
        userRepository.save(user);

        return "user logged in successfully";

    }

    @Override
    public UserDetailsResponseDto validateToken(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Token does not exist");
        }

        Token existingToken = optionalToken.get();

        if(existingToken.getExpireAt().isBefore(LocalDateTime.now()) || existingToken.isDeleted()) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        User user = existingToken.getUser();
        UserDetailsResponseDto response = new UserDetailsResponseDto();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setGender(user.getGender());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());

        return response;

    }

    @Override
    public String logoutUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Token does not exist");
        }

        Token existingToken = optionalToken.get();
        existingToken.setActive(false);
        existingToken.setDeleted(true);
        tokenRepository.save(existingToken);

        return "user logged out successfully";
    }

    private Token generateToken(User user) {
        String token = UUID.randomUUID().toString();
        Token newToken = new Token();
        newToken.setCreatedAt(LocalDateTime.now());
        newToken.setIssuedAt(LocalDateTime.now());
        newToken.setActive(true);

        LocalDateTime current = LocalDateTime.now();
        LocalDateTime expiry = current.plusMonths(1);
        newToken.setExpireAt(expiry);
        newToken.setToken(token);
        newToken.setUser(user);

        return newToken;
    }
}
