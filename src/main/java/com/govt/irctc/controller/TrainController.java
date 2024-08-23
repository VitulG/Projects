package com.govt.irctc.controller;

import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.TrainAdvice.TrainCreationException;
import com.govt.irctc.advice.TrainAdvice.TrainDeletionException;
import com.govt.irctc.advice.TrainAdvice.TrainNotFoundException;
import com.govt.irctc.advice.TrainAdvice.TrainUpdationException;
import com.govt.irctc.advice.UnauthorizedUserException;
import com.govt.irctc.dto.TrainDto;
import com.govt.irctc.service.trainservice.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/trains")
public class TrainController {
    private final TrainService trainService;

    @Autowired
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping("/add-train")
    public ResponseEntity<String> addTrains(@RequestBody TrainDto trainDto,
                                            @RequestHeader("Authorization") String token) {
        try {
            String message = trainService.addTrain(trainDto, token);
            if(message == null || message.isEmpty()) {
                throw new TrainCreationException("Unable to add the train");
            }
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }catch(TrainCreationException trainCreationException) {
            trainCreationException.printStackTrace();
            return new ResponseEntity<>(trainCreationException.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(InvalidTokenException | UnauthorizedUserException securityException) {
            securityException.printStackTrace();
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Unable to add the train", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-train/{trainNumber}")
    public ResponseEntity<TrainDto> getTrainByTrainNumber(@PathVariable("trainNumber") Long trainNumber,
                                                          @RequestHeader("Authorization") String token) {
        try {
            TrainDto trainDto = trainService.getTrainById(trainNumber, token);
            if(trainDto == null) {
                throw new TrainNotFoundException("train is not available");
            }
            return new ResponseEntity<>(trainDto, HttpStatus.OK);
        }catch (TrainNotFoundException trainNotFoundException) {
            trainNotFoundException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch(InvalidTokenException invalidTokenException) {
            invalidTokenException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-trains")
    public ResponseEntity<List<TrainDto>> getAllTrains(@RequestHeader("Authorization") String token) {
        try {
            List<TrainDto> trains = trainService.getAllTrains(token);
            if(trains.isEmpty()) {
                throw new TrainNotFoundException("trains not found");
            }
            return new ResponseEntity<>(trains, HttpStatus.OK);
        }catch (TrainNotFoundException trainNotFoundException) {
            trainNotFoundException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch(InvalidTokenException invalidTokenException) {
            invalidTokenException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/trains/{trainNumber}")
    public ResponseEntity<String> updateTrainById(@PathVariable("trainNumber") Long trainNumber, @RequestBody TrainDto trainDto,
                                                  @RequestHeader("Authorization") String token) {
        try {
            String message = trainService.updateTrainById(trainNumber, trainDto, token);
            if(message == null || message.isEmpty()) {
                throw new TrainUpdationException("unable to update the train");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch(InvalidTokenException | UnauthorizedUserException securityException) {
            securityException.printStackTrace();
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (TrainNotFoundException trainNotFoundException) {
            trainNotFoundException.printStackTrace();
            return new ResponseEntity<>(trainNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch(TrainUpdationException trainUpdationException) {
            trainUpdationException.printStackTrace();
            return new ResponseEntity<>(trainUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete-train/{trainNumber}")
    public ResponseEntity<String> deleteTrainByNumber(@PathVariable("trainNumber") Long trainNumber,
                                                      @RequestHeader("Authorization") String token) {
        try {
            String message = trainService.deleteTrainByTrainNumber(trainNumber, token);
            if(message == null || message.isEmpty()) {
                throw new TrainDeletionException("Train not available");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (TrainDeletionException trainDeletionException) {
            trainDeletionException.printStackTrace();
            return new ResponseEntity<>(trainDeletionException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(InvalidTokenException | UnauthorizedUserException securityException) {
            securityException.printStackTrace();
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(TrainNotFoundException trainNotFoundException) {
            trainNotFoundException.printStackTrace();
            return new ResponseEntity<>(trainNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
