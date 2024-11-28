package com.rggupta.HouseHopper.controllers;

import com.rggupta.HouseHopper.dto.LoginRequestDto;
import com.rggupta.HouseHopper.dto.SignupRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestBody SignupRequestDto signupRequestDto) {
        return null;
    }
}
