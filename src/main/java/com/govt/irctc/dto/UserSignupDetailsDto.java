package com.govt.irctc.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
