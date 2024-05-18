package com.scaler.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeatherDto {
    private Double current_temperature_in_C;
    private Double current_temperature_in_F;
    private Double wind_speed_in_miles;
    private Double wind_speed_in_kph;
    private int humidity;
}
