package com.govt.irctc.strategy;

import com.govt.irctc.enums.CompartmentType;
import org.springframework.stereotype.Component;

@Component
public class FareCalculationStrategyImpl implements FareCalculationStrategy {
    @Override
    public double calculateFare(CompartmentType compartmentType, double distance) {
        if(compartmentType == CompartmentType.FIRST_AC) {
            return distance * 2.5d;
        }else if(compartmentType == CompartmentType.SECOND_AC) {
            return distance * 1.8d;
        }else if(compartmentType == CompartmentType.THIRD_AC) {
            return distance * 1.35d;
        }else if(compartmentType == CompartmentType.SLEEPER) {
            return distance * 1.25d;
        }else {
            return distance * 0.825d;
        }
    }
}
