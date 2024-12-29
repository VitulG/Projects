package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityDetailsDto {
    private String cityName;
    private String state;
    private String country;
    private String zipCode;
    private double latitude;
    private double longitude;
    private String stationName;
}
