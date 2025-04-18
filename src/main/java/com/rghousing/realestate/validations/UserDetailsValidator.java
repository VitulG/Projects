package com.rghousing.realestate.validations;

import com.rghousing.realestate.dto.UserDto;
import com.rghousing.realestate.exceptions.UserCreationException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsValidator {
    public void validateUserSignupDetails(UserDto userDetails) throws UserCreationException {
        validateUserName(userDetails.getUsername());
        validateUserPassword(userDetails.getPassword());
        validateUserEmail(userDetails.getEmail());
        validateUserPhoneNumber(userDetails.getPhoneNumber());
    }
    public void validateUserPassword(String password) throws UserCreationException {
        if(password.length() < 6) {
            throw new UserCreationException("Password must be contain at least 6 length");
        }

        boolean hasDigits = false;
        boolean hasSpacialCharacter = false;

        for(char ch : password.toCharArray()) {
            if(Character.isDigit(ch)) {
                hasDigits = true;
            }
            if("~!@#$%&*_+".indexOf(ch) != -1) {
                hasSpacialCharacter = true;
            }
        }

        if(!hasSpacialCharacter) {
            throw new UserCreationException("password must contain at least one special symbol ~ ! @ # $ % & * _ or +");
        }

        if(!hasDigits) {
            throw new UserCreationException("password must contain at least one digit");
        }
    }

    public void validateUserName(String userName) throws UserCreationException {
        if(userName == null || userName.isEmpty()) {
            throw new UserCreationException("Username cannot be empty");
        }

        if(!Character.isUpperCase(userName.charAt(0))) {
            throw new UserCreationException("Username must start with upper case letters");
        }

        String regex = "^[A-Z][a-zA-Z]*(\\\\s(\\\\.|[a-zA-Z]+))?$";

        if (!userName.matches(regex)) {
            throw new UserCreationException("Username must start with a first name, optionally followed by a space and a last name or '.'");
        }

    }

    public void validateUserEmail(String userEmail) throws UserCreationException {
        if(userEmail == null || userEmail.isEmpty()) {
            throw new UserCreationException("User email cannot be empty");
        }

        String regex = "^[A-Za-z]\\\\w{6,29}$";

        if (!userEmail.matches(regex)) {
            throw new UserCreationException("invalid email address");
        }
    }

    public void validateUserPhoneNumber(String userPhoneNumber) throws UserCreationException {
        if(userPhoneNumber == null || userPhoneNumber.isEmpty()) {
            throw new UserCreationException("User phone number cannot be empty");
        }
        String phoneRegex = "^[6-9]\\d{9}$";
        if (!userPhoneNumber.matches(phoneRegex)) {
            throw new UserCreationException("invalid phone number");
        }
    }
}
