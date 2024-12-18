package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserSignupDetailsDto {
    private String username;
    private String password;
    private String userEmail;
    private int userAge;
    private String userGender;
    private Long userPhoneNumber;
    private Date userDob;
    private String userRole;
    private String profilePictureUrl;
    private int houseNumber;
    private String streetName;
    private String city;
    private String state;
    private String pinCode;
    private String country;
}
