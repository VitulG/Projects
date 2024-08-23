package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userName;
    private String userEmail;
    private int userAge;
    private char userGender;
    private Long userPhoneNumber;
    private String userAddress;
    private Date userDob;
    private List<BookingDto> userBookings;
}
