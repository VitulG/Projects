package com.social.connectify.validations;

import com.social.connectify.dto.UserDetailsDto;
import com.social.connectify.dto.UserUpdateDetailsDto;
import com.social.connectify.exceptions.UserCreationException;
import com.social.connectify.exceptions.UserUpdationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserUpdateDetailsValidator {
    public void validateUserUpdateDetails(UserUpdateDetailsDto userUpdateDetailsDto) throws UserUpdationException {
        if(!checkUpdatedFirstName(userUpdateDetailsDto.getFirstName())) {
            throw new UserUpdationException("first name is not valid. " +
                    "First name must start with a capital letter and contain only letters");
        }

        if(!checkUpdatedLastName(userUpdateDetailsDto.getLastName())) {
            throw new UserUpdationException("last name is not valid. " +
                    "Last name must start with a capital letter and contain only letters and numbers");
        }

        if(!checkUpdatedEmail(userUpdateDetailsDto.getEmail())) {
            throw new UserUpdationException("email is not valid. " +
                    "Email must be in the format 'example@example.com'");
        }

        if(!checkUserPassword(userUpdateDetailsDto.getPassword())) {
            throw new UserUpdationException("password is not valid. " +
                    "Password must contain at least 8 characters, including uppercase, lowercase, " +
                    "numbers, and special characters");
        }

        if(!checkUpdatedPhoneNumber(userUpdateDetailsDto.getPhoneNumber())) {
            throw new UserUpdationException("phone number is not valid. " +
                    "Phone number must be 10 digit long and must contain digits only");
        }

        if(!checkUpdatedDateOfBirth(userUpdateDetailsDto.getDateOfBirth())) {
            throw new UserUpdationException("date of birth is not valid. " +
                    "Date of birth must be in the format 'yyyy-MM-dd' and " +
                    "must be a past or present date");
        }

        if(!checkUpdatedGender(userUpdateDetailsDto.getGender())) {
            throw new UserUpdationException("gender is not valid. " +
                    "Gender must be either 'Male(M)' or 'Female(F)' or 'Transgender(T)'" +
                    "and contains only one character");
        }
    }
    private boolean checkUpdatedFirstName(String name) {
        return name.matches("^[A-Z][a-z]*$");
    }

    private boolean checkUpdatedLastName(String lastName) {
        return lastName.matches("^[A-Z][a-z]+\\d*$");
    }

    private boolean checkUpdatedEmail(String email) {
        return email.matches("^(?![_.-])(?=.{1,64}@)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean checkUserPassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    }

    private boolean checkUpdatedPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    private boolean checkUpdatedDateOfBirth(Date dateOfBirth) {
        // Implement date validation logic here
        Date today = new Date();
        return dateOfBirth.before(today);
    }

    private boolean checkUpdatedGender(String gender) {
        return gender.matches("^[MFT]$");
    }
}
