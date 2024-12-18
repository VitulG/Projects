package com.govt.irctc.validation;

import com.govt.irctc.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class UserDetailsValidator {

    public boolean validateUserName(String userName) {
        if(userName == null || userName.length() < 3 || userName.length() > 256) {
            return false;
        }
        String regex = "^[A-Z][a-z0-9]{0,254} [A-Z][a-z0-9]{0,254}$";
        return userName.matches(regex);
    }

    public boolean validatePassword(String password) {
        if(password == null || password.length() < 5 || password.length() > 32) {
            return false;
        }
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{5,32}$";
        return password.matches(regex);
    }

    public boolean validateEmail(String email) {
        if(email == null || email.length() < 5 || email.length() > 32) {
            return false;
        }
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    public boolean validateUserAge(int age) {
        return age > 0 && age < 125;
    }

    public boolean validatePhoneNumber(Long phoneNumber) {
        if(phoneNumber == null) {
            return false;
        }
        String number = phoneNumber.toString();
        return (number.length() == 10) && number.matches("\\d+");
    }

    public boolean validateUserGender(String gender) {
        return (gender.equalsIgnoreCase("male")) || (gender.equalsIgnoreCase("female")) ||
                (gender.equalsIgnoreCase("other"));
    }

    public boolean validateUserDateOfBirth(Date dateOfBirth) {
        if(dateOfBirth == null) {
            return false;
        }
        return dateOfBirth.before(new Date());
    }

    public boolean validateUserRole(String role) {
        if(role == null || role.isEmpty()) {
            return false;
        }
        return role.equalsIgnoreCase(UserRole.USER.toString()) ||
                role.equalsIgnoreCase(UserRole.ADMIN.toString());
    }

}
