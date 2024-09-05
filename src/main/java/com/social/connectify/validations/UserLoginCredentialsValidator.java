package com.social.connectify.validations;

import com.social.connectify.dto.UserLoginDetailsDto;
import com.social.connectify.exceptions.InvalidCredentialsException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.User;
import com.social.connectify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserLoginCredentialsValidator {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserLoginCredentialsValidator(BCryptPasswordEncoder bCryptPasswordEncoder,
                                         UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    public void validateUserLoginDetails(UserLoginDetailsDto userLoginDetailsDto)
            throws InvalidCredentialsException, UserNotFoundException {
        Optional<User> user = userRepository.findUserByEmail(userLoginDetailsDto.getEmail());

        if(user.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        if(!checkUserEmail(userLoginDetailsDto.getEmail(), user.get().getEmail())) {
           throw new InvalidCredentialsException("Invalid user email.");
       }

       if(!checkUserPassword(user.get().getPassword(), userLoginDetailsDto.getPassword())) {
           throw  new InvalidCredentialsException("Invalid user password.");
       }
    }

    private boolean checkUserEmail(String email, String savedEmail) {
        return email.equals(savedEmail);
    }

    private boolean checkUserPassword(String savedPassword, String enteredPassword) {
        if(enteredPassword.isEmpty()) {
            return false;
        }
        return bCryptPasswordEncoder.matches(enteredPassword, savedPassword);
    }
}
