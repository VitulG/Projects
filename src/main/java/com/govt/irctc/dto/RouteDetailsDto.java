package com.govt.irctc.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteDetailsDto {
    private String routeNumber;
    private Long trainNumber;
    private String stationName;
    private String routeStatus;
}
