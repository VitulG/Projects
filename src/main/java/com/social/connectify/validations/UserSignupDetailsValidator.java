package com.social.connectify.validations;

import com.social.connectify.dto.UserDetailsDto;
import com.social.connectify.exceptions.UserCreationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserSignupDetailsValidator {
    // Implement validation logic here
    public void validateUserSignupDetails(UserDetailsDto userDetailsDto) throws UserCreationException {
        if(!checkFirstName(userDetailsDto.getFirstName())) {
            throw new UserCreationException("first name is not valid. " +
                    "First name must start with a capital letter and contain only letters");
        }

        if(!checkLastName(userDetailsDto.getLastName())) {
            throw new UserCreationException("last name is not valid. " +
                    "Last name must start with a capital letter and contain only letters and numbers");
        }

        if(!checkEmail(userDetailsDto.getEmail())) {
            throw new UserCreationException("email is not valid. " +
                    "Email must be in the format 'example@example.com'");
        }

        if(!checkPassword(userDetailsDto.getPassword())) {
            throw new UserCreationException("password is not valid. " +
                    "Password must contain at least 8 characters, including uppercase, lowercase, " +
                    "numbers, and special characters");
        }

        if(!checkPhoneNumber(userDetailsDto.getPhoneNumber())) {
            throw new UserCreationException("phone number is not valid. " +
                    "Phone number must be 10 digit long and must contain digits only");
        }

        if(!checkDateOfBirth(userDetailsDto.getDateOfBirth())) {
            throw new UserCreationException("date of birth is not valid. " +
                    "Date of birth must be in the format 'yyyy-MM-dd' and " +
                    "must be a past or present date");
        }

        if(!checkGender(userDetailsDto.getGender())) {
            throw new UserCreationException("gender is not valid. " +
                    "Gender must be either 'Male(M)' or 'Female(F)' or 'Transgender(T)'" +
                    "and contains only one character");
        }
    }
    private boolean checkFirstName(String name) {
        return name.matches("^[A-Z][a-z]*$");
    }

    private boolean checkLastName(String lastName) {
        return lastName.matches("^[A-Z][a-z]+\\d*$");
    }

    private boolean checkEmail(String email) {
        return email.matches("^(?![_.-])(?=.{1,64}@)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean checkPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    private boolean checkDateOfBirth(Date dateOfBirth) {
        // Implement date validation logic here
        Date today = new Date();
        return dateOfBirth.before(today);
    }

    private boolean checkGender(String gender) {
        return gender.matches("^[MFT]$");
    }
}
