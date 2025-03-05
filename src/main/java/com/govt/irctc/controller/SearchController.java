package com.govt.irctc.controller;

import com.govt.irctc.dto.TrainDto;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.service.trainservice.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final TrainService trainService;

    @Autowired
    public SearchController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/trains")
    public HttpEntity<List<TrainDto>> searchTrains(@RequestParam String trainName) {
        try {
            List<TrainDto> searchedTrains = trainService.searchTrainsByName(trainName);
            return new ResponseEntity<>(searchedTrains, HttpStatus.OK);
        } catch (TrainNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
