package com.govt.irctc.controller;

import com.govt.irctc.dto.PlatformDetailsDto;
import com.govt.irctc.exceptions.PlatformExceptions.PlatformCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.StationException.StationNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.platformservice.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/platforms")
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @PostMapping("/add-platform")
    public ResponseEntity<String> addPlatform(@RequestBody PlatformDetailsDto detailsDto, @RequestHeader("Authorization") String token) {
        try {
            String response = platformService.addPlatform(detailsDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TokenNotFoundException | TrainNotFoundException | StationNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (PlatformCreationException creationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creationException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
