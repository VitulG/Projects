package com.scaler.weather.service;

import org.springframework.stereotype.Service;

public interface WeatherService {
    public String getCurrentCityTemperature(String cityName, String airQualityIndex);
}
