package com.govt.irctc.controller;

import com.govt.irctc.dto.ScheduleDetailsDto;
import com.govt.irctc.dto.ScheduleDto;
import com.govt.irctc.exceptions.ScheduleExceptions.ScheduleCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.scheduleservice.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/add-schedule/{trainNumber}")
    public ResponseEntity<String> addSchedule(@RequestBody ScheduleDetailsDto scheduleDetailsDto,
                                              @PathVariable("trainNumber") Long trainNumber,
                                              @RequestHeader("Authorization") String token) {
        try {
            String response = scheduleService.setTrainSchedule(token, trainNumber, scheduleDetailsDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(securityException.getMessage());
        } catch (TokenNotFoundException | TrainNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
        } catch (ScheduleCreationException creationException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(creationException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }

    @GetMapping("/get-schedule/{trainNumber}")
    public ResponseEntity<List<ScheduleDto>> getSchedule(@PathVariable("trainNumber") Long trainNumber,
                                                               @RequestHeader("Authorization") String token) {
        try {
            List<ScheduleDto> schedules = scheduleService.getTrainSchedules(token, trainNumber);
            return ResponseEntity.status(HttpStatus.OK).body(schedules);
        } catch (TokenNotFoundException | TrainNotFoundException notFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        } catch (InvalidTokenException securityException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
}
