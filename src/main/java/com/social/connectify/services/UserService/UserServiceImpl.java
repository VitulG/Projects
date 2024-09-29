package com.social.connectify.services.UserService;

import com.social.connectify.dto.ChangePasswordRequestDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserUpdateDetailsDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PasswordMismatchException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.exceptions.UserUpdationException;
import com.social.connectify.models.Group;
import com.social.connectify.enums.GroupStatus;
import com.social.connectify.models.Token;
import com.social.connectify.models.User;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import com.social.connectify.validations.UserUpdateDetailsValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserUpdateDetailsValidator userUpdateDetailsValidator;

    @Autowired
    public UserServiceImpl(TokenRepository tokenRepository, UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPassword, UserUpdateDetailsValidator userUpdateDetailsValidator) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPassword;
        this.userUpdateDetailsValidator = userUpdateDetailsValidator;
    }

    @Override
    @Transactional
    public UserDetailsResponseDto getUserDetails(String token) throws InvalidTokenException {
        User user = validateAndGetUser(token);

        UserDetailsResponseDto response = new UserDetailsResponseDto();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setGender(user.getGender());

        return response;
    }

    @Override
    @Transactional
    public String updateUserDetails(String token, UserUpdateDetailsDto userUpdateDetailsDto) throws InvalidTokenException,
            UserNotFoundException, PasswordMismatchException, UserUpdationException {

        User user = validateAndGetUser(token);

        userUpdateDetailsValidator.validateUserUpdateDetails(userUpdateDetailsDto);

        if(user.isDeleted()) {
            throw new UserNotFoundException("either user deactivated or account is deleted");
        }

        if(!bCryptPasswordEncoder.matches(userUpdateDetailsDto.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("password does not match");
        }

        Optional<User> anotherUser = userRepository.findUserByEmail(userUpdateDetailsDto.getEmail());

        if(anotherUser.isPresent() && !anotherUser.get().getUserId().equals(user.getUserId())) {
            throw new UserUpdationException("email already exists");
        }

        user.setFirstName(userUpdateDetailsDto.getFirstName());
        user.setLastName(userUpdateDetailsDto.getLastName());
        user.setEmail(userUpdateDetailsDto.getEmail());
        user.setPhoneNumber(userUpdateDetailsDto.getPhoneNumber());
        user.setGender(userUpdateDetailsDto.getGender());
        user.setDateOfBirth(userUpdateDetailsDto.getDateOfBirth());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return "user details updated successfully";
    }

    @Override
    public String changeUserPassword(String token, ChangePasswordRequestDto changePasswordRequestDto) throws InvalidTokenException, UserNotFoundException, PasswordMismatchException {

        User user = validateAndGetUser(token);

        if(user.isDeleted()) {
            throw new UserNotFoundException("User is either deleted or deactivated");
        }

        if(bCryptPasswordEncoder.matches(changePasswordRequestDto.getNewPassword(), user.getPassword())) {
            throw new PasswordMismatchException("New password cannot be the same as the old password");
        }

        if(!bCryptPasswordEncoder.matches(changePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Old password does not match");
        }

        String newPassword = bCryptPasswordEncoder.encode(changePasswordRequestDto.getNewPassword());

        if(!bCryptPasswordEncoder.matches(changePasswordRequestDto.getConfirmPassword(), newPassword)) {
            throw new PasswordMismatchException("Confirm password does not match");
        }

        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return "password updated successfully";
    }

    @Override
    public String deleteUserAccount(String token) throws InvalidTokenException {

        User user = validateAndGetUser(token);

        for(Token userTokens : user.getUserTokens()) {
            userTokens.setActive(false);
            userTokens.setDeleted(true);
        }

        user.setDeleted(true);
        userRepository.save(user);

        return "user deleted successfully";
    }

    @Override
    @Transactional
    public List<String> getUserGroups(String token) throws InvalidTokenException {
        User user = validateAndGetUser(token);
        return user.getUserGroups()
                .stream()
                .filter(group -> group.getGroupStatus() == GroupStatus.ACTIVE)
                .filter(group -> !group.isDeleted())
                .map(Group::getGroupName)
                .toList();
    }

    private User validateAndGetUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Token is invalid");
        }

        Token existingToken = optionalToken.get();

        if(existingToken.isDeleted() || !existingToken.isActive() || existingToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("either token is invalid or not active");
        }
        return existingToken.getUser();
    }

}
