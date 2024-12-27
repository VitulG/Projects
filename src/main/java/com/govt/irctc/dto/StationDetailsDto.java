package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StationDetailsDto {
    private String stationName;
    private String city;
    private String stationStatus;
    private List<Long> trains;
    private List<String> routes;
}
