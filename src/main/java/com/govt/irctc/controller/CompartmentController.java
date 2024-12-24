package com.govt.irctc.controller;

import com.govt.irctc.dto.CompartmentDetailsDto;
import com.govt.irctc.exceptions.CompartmentException.CompartmentCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.compartmentservice.CompartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compartments")
public class CompartmentController {
    private final CompartmentService compartmentService;

    @Autowired
    public CompartmentController(CompartmentService compartmentService) {
        this.compartmentService = compartmentService;
    }

    @PostMapping("/add-compartment")
    public ResponseEntity<String> addCompartment(@RequestBody CompartmentDetailsDto compartmentDetailsDto,
                                                 @RequestHeader("Authorization") String token) {
        try {
            String response = compartmentService.addCompartment(compartmentDetailsDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TrainNotFoundException | TokenNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (CompartmentCreationException creationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creationException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
