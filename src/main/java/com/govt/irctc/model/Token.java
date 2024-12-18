package com.govt.irctc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token extends BaseModel{
    @Column(nullable = false, unique = true)
    private String tokenValue;

    @ManyToOne
    private User userTokens;

    @Column(nullable = false)
    private Date tokenValidity;
}
