package com.govt.irctc.strategy;

import com.govt.irctc.enums.CompartmentType;

public interface FareCalculationStrategy {
    public double calculateFare(CompartmentType compartmentType, double distance);
}
