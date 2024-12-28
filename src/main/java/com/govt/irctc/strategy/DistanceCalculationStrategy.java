package com.govt.irctc.strategy;

public interface DistanceCalculationStrategy {
    double calculateDistance(double sourceLat, double sourceLong, double destinationLat, double destinationLong);
}
