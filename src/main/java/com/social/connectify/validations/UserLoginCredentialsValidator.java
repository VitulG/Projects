package com.social.connectify.validations;

import com.social.connectify.dto.UserLoginDetailsDto;
import com.social.connectify.exceptions.InvalidCredentialsException;
import com.social.connectify.exceptions.UserNameMismatchException;
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

    @Autowired
    public UserLoginCredentialsValidator(BCryptPasswordEncoder bCryptPasswordEncoder,
                                         UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void validateUserLoginDetails(UserLoginDetailsDto userLoginDetailsDto, User user)
            throws InvalidCredentialsException, UserNameMismatchException {

        if(!checkUserEmail(userLoginDetailsDto.getEmail(), user.getEmail())) {
           throw new InvalidCredentialsException("Invalid user email.");
       }

       if(!checkUserPassword(user.getPassword(), userLoginDetailsDto.getPassword())) {
           throw  new InvalidCredentialsException("Invalid user password.");
       }

       // check user name
       String userName = user.getFirstName()+" "+user.getLastName();
       String currentUserName = userLoginDetailsDto.getUserName();

       if(!currentUserName.equals(userName)) {
           throw new UserNameMismatchException("user name does not match. please try again");
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
