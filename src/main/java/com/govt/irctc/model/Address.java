package com.govt.irctc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address extends BaseModel {
    private int houseNumber;
    private String street;
    private String city;
    private String state;
    private String country;
    private String pinCode;

    @ManyToOne
    private User user;
}
