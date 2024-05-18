package com.scaler.weather.controller;

import com.scaler.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    private WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RequestMapping(value="/weather", method = RequestMethod.GET)
    public ResponseEntity<String> getTemperature(@RequestParam String cityName,
                                                 @RequestParam String airQualityIndex) {
        String temp = weatherService.getCurrentCityTemperature(cityName, airQualityIndex);
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

}
