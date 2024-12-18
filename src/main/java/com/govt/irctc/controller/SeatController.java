package com.govt.irctc.controller;

import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;
import com.govt.irctc.exceptions.SeatExceptions.SeatDeletionException;
import com.govt.irctc.exceptions.SeatExceptions.SeatNotCreatedException;
import com.govt.irctc.exceptions.SeatExceptions.SeatUpdationException;
import com.govt.irctc.exceptions.SeatExceptions.SeatsNotFoundException;
import com.govt.irctc.service.seatservice.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seats")
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("/add-seats")
    public ResponseEntity<String> addSeats(@RequestBody SeatDto seatDto,
                                           @RequestHeader("Authorization") String token) {
        try {
            String message = seatService.addSeats(seatDto, token);

            if(message == null || message.isEmpty()) {
                throw new SeatNotCreatedException("Seats was not created");
            }
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch(SeatNotCreatedException seatException) {
            seatException.printStackTrace();
            return new ResponseEntity<>(seatException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/seats/{trainNumber}")
    public ResponseEntity<ShowSeatDto> getTrainSeats(@PathVariable("trainNumber") Long trainNumber,
                                                     @RequestHeader("Authorization") String token) {
        try {
            ShowSeatDto seats = seatService.showSeats(trainNumber, token);
            if(seats == null) {
                throw new SeatsNotFoundException("Seats are not available for this train");
            }
            return new ResponseEntity<>(seats, HttpStatus.OK);
        } catch(SeatsNotFoundException seatsNotFoundException) {
            seatsNotFoundException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-seats/{trainNumber}")
    public ResponseEntity<String> updateByTrainNumber(@PathVariable Long trainNumber,
                                                         @RequestBody SeatDto seatDto,
                                                         @RequestHeader("Authorization") String token){
        try {
            String message = seatService.updateSeatsByTrainNumber(trainNumber, seatDto, token);
            if(message == null || message.isEmpty()) {
                throw new SeatUpdationException("unable to update the seats");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch(SeatUpdationException seatUpdationException) {
            seatUpdationException.printStackTrace();
            return new ResponseEntity<>(seatUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-seats/{trainNumber}")
    public ResponseEntity<String> deleteTrainSeats(@PathVariable("trainNumber") Long trainNumber,
                                                   @RequestHeader("Authorization") String token){
        try {
            String message = seatService.deleteSeatsByTrainNumber(trainNumber, token);
            if(message == null || message.isEmpty()) {
                throw new SeatDeletionException("unable to delete the seats");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (SeatDeletionException exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
