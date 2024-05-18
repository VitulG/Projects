package com.scaler.weather.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AirQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aqId;

    private double co2;
    private double no2;
    private double o3;
    private double so2;
    private double pm25;
    private double pm10;
    private int us_epa_index;
}
