package com.govt.irctc.controller;

import com.govt.irctc.dto.BookingDetailsDto;

import com.govt.irctc.exceptions.BookingExceptions.BookingCreatingException;
import com.govt.irctc.exceptions.CityExceptions.CityNotFoundException;
import com.govt.irctc.exceptions.CompartmentException.CompartmentNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.bookingservice.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RequestMapping(value = "/book-ticket", method = RequestMethod.POST)
    public ResponseEntity<String> bookTickets(@RequestBody BookingDetailsDto bookingDetailsDto, @RequestHeader("Authorization") String token) {
        try {
            String message = bookingService.bookTickets(bookingDetailsDto, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (InvalidTokenException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TokenNotFoundException | TrainNotFoundException | CompartmentNotFoundException |
                 CityNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (BookingCreatingException creatingException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creatingException.getMessage());
        }
    }
}