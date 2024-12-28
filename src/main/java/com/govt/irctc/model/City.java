package com.govt.irctc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class City extends BaseModel {
    private String cityName;
    private String state;
    private String country;
    private String zipCode;
    private double latitude;
    private double longitude;

    @OneToOne
    private Station station;
}
