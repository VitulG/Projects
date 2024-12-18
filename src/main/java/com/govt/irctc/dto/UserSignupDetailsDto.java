package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserSignupDetailsDto {
    private String userName;
    private String password;
    private String userEmail;
    private int userAge;
    private char userGender;
    private Long userPhoneNumber;
    private String userAddress;
    private Date userDob;
    private String userRole;
}
