package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private int houseNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}
