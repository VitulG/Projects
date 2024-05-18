package com.scaler.weather.service;

import com.scaler.weather.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherComServiceImpl implements WeatherService{

    private RestTemplate restTemplate;
    private LocationRepository locationRepository;

    @Autowired
    public WeatherComServiceImpl(RestTemplate restTemplate,
                                 LocationRepository locationRepository) {
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
    }

    @Override
    public String getCurrentCityTemperature(String cityName, String airQualityIndex) {
        String weatherComApiKey = "8688dd86be9746c697e74640241805";
        String baseUrl = "https://api.weatherapi.com/v1/current.json?key=" + weatherComApiKey
                + "&q="+cityName+"&aqi="+ airQualityIndex;

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl,
                String.class
        );

        System.out.println(response.getBody());

        if(response.getBody() == null) {
            return "NOT FOUND";
        }



        return response.getBody();
    }
}
