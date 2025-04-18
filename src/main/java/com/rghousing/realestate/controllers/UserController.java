package com.rghousing.realestate.controllers;

import com.rghousing.realestate.dto.ResponseDto;
import com.rghousing.realestate.dto.UserDto;
import com.rghousing.realestate.exceptions.UserCreationException;
import com.rghousing.realestate.services.userservice.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // here I can give the login and sign up functionality for the user
    @PostMapping("/signup-user")
    public ResponseEntity<ResponseDto> signupUser(@RequestBody UserDto user) {
        ResponseDto responseDto = new ResponseDto();
        try {
            String message = userService.signupUser(user);
            responseDto.setMessage(message);
            responseDto.setStatus(HttpStatus.CREATED);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (UserCreationException creationException) {
            responseDto.setErrorMessage(creationException.getMessage());
            responseDto.setStatus(HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            responseDto.setErrorMessage(exception.getMessage());
            responseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
