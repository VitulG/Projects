package com.govt.irctc.controller;

import com.govt.irctc.dto.StationDetailsDto;
import com.govt.irctc.exceptions.CityExceptions.CityNotFoundException;
import com.govt.irctc.exceptions.RouteException.RouteNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.stationservice.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping("/add-station")
    public ResponseEntity<String> addStation(@RequestBody StationDetailsDto stationDetails,
                                             @RequestHeader("Authorization") String token) {
        try {
            String response = stationService.addStation(stationDetails, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TrainNotFoundException | TokenNotFoundException | RouteNotFoundException |
                 CityNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
