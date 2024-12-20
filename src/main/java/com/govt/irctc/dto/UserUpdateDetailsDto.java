package com.govt.irctc.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
public class UserUpdateDetailsDto {
    private String updatedUserName;
    private String updatedEmail;
    private int updatedAge;
    private String updatedGender;
    private Long updatedPhoneNumber;
    private Date userDob;
    private String profilePictureUrl;
}
