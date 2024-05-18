package com.scaler.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirQualityDto {
    private double co2;
    private double no2;
    private double o3;
    private double so2;
    private double pm25;
    private double pm10;
    private int us_epa_index;
}
