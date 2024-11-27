package com.example.personal.EventSphere.controller;

import com.example.personal.EventSphere.Dto.CreateEventRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Event")
public class EventController {

    @PostMapping("/createEvent")
    public ResponseEntity<String> createEvent(@RequestBody CreateEventRequestDto createEventRequestDto) {

    }
}
