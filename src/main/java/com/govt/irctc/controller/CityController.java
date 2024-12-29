package com.govt.irctc.controller;

import com.govt.irctc.dto.CityDetailsDto;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.service.cityservice.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
public class CityController {
    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping("/add-city")
    public ResponseEntity<String> addCity(@RequestBody CityDetailsDto cityDetailsDto, @RequestHeader("Authorization") String token) {
        try {
            String response = cityService.addCity(cityDetailsDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TokenNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
