package com.social.connectify.controllers;

import com.social.connectify.dto.ChangePasswordRequestDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserUpdateDetailsDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PasswordMismatchException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.exceptions.UserUpdationException;
import com.social.connectify.services.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetailsResponseDto> getUserProfile(@RequestHeader("Authorization")
                                                                     String token) {
        try {
            UserDetailsResponseDto userDetailsResponse =  userService.getUserDetails(token);
            return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
        }catch(InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<String> updateUserProfile(@RequestHeader("Authorization") String token ,
                                                    @RequestBody UserUpdateDetailsDto userUpdateDetailsDto) {
        try {
            String response = userService.updateUserDetails(token, userUpdateDetailsDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch(PasswordMismatchException passwordMismatchException) {
            return new ResponseEntity<>(passwordMismatchException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token,
                                                   @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        try {
            String response = userService.changeUserPassword(token, changePasswordRequestDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch(PasswordMismatchException passwordMismatchException) {
            return new ResponseEntity<>(passwordMismatchException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String token) {
        try {
            String response = userService.deleteUserAccount(token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        }  catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-groups")
    public ResponseEntity<List<String>> userGroups(@RequestHeader("Authorization") String token) {
        try {
            List<String> userGroups = userService.getUserGroups(token);
            return new ResponseEntity<>(userGroups, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // add the 2-factor authentication method here
}
