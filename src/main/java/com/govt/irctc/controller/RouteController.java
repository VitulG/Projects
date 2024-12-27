package com.govt.irctc.controller;

import com.govt.irctc.dto.RouteDetailsDto;
import com.govt.irctc.exceptions.RouteException.RouteCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.routeservice.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route")
public class RouteController {
    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping("/add-route")
    public ResponseEntity<String> addRoute(@RequestBody RouteDetailsDto routeDetailsDto, @RequestHeader("Authorization")
                                           String token) {
        try {
            String response = routeService.addRoute(routeDetailsDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TokenNotFoundException | TrainNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (RouteCreationException creationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creationException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
