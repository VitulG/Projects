package com.govt.irctc.strategy;

import org.springframework.stereotype.Component;

@Component
public class DistanceCalculationStrategyImpl implements DistanceCalculationStrategy {
    private static final double EARTH_RADIUS = 6371;

    @Override
    public double calculateDistance(double srcLatitude, double srcLongitude, double destLatitude, double destLongitude) {
        double lat1Rad = Math.toRadians(srcLatitude);
        double lon1Rad = Math.toRadians(srcLongitude);
        double lat2Rad = Math.toRadians(destLatitude);
        double lon2Rad = Math.toRadians(destLongitude);

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Apply Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Distance in km
    }
}
