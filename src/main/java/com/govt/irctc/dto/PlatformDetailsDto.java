package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatformDetailsDto {
    private int platformNumber;
    private String platformStatus;
    private String stationName;
    private Long trainNumber;
}
