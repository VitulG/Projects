package com.social.connectify.controllers;

import com.social.connectify.dto.UserDetailsDto;
import com.social.connectify.dto.UserDetailsResponseDto;
import com.social.connectify.dto.UserLoginDetailsDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.services.LoginService.LoginService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/firstPage")
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/signUp")
    @Transactional
    public ResponseEntity<String> signUpUser(@RequestBody  UserDetailsDto userDetailsDto) {
        try {
            String response = loginService.signUpUser(userDetailsDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (UserAlreadyExistsException userAlreadyExistsException) {
            return new ResponseEntity<>("User already exists", HttpStatus.BAD_REQUEST);
        }catch (UserCreationException userCreationException) {
            return new ResponseEntity<>(userCreationException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch(PasswordMismatchException passwordMismatchException) {
            return new ResponseEntity<>(passwordMismatchException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<String> loginUser(@RequestBody UserLoginDetailsDto userLoginDetailsDto) {
        try {
            String response = loginService.loginUser(userLoginDetailsDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }catch (InvalidCredentialsException | PasswordMismatchException invalidCredentialsException) {
            return new ResponseEntity<>(invalidCredentialsException.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (UserNameMismatchException userNameMismatchException) {
            return new ResponseEntity<>(userNameMismatchException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (SessionAlreadyActiveException sessionAlreadyActiveException) {
            return new ResponseEntity<>(sessionAlreadyActiveException.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception ex) {
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validateToken")
    @Transactional
    public ResponseEntity<UserDetailsResponseDto> validateToken(@RequestHeader("Authorization")
                                                                    String token) {
        try {
            UserDetailsResponseDto userDetails = loginService.validateToken(token);
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        }catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }catch(Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout/{token}")
    @Transactional
    public ResponseEntity<String> logoutUser(@PathVariable("token") String token) {
        try {
            String message = loginService.logoutUser(token);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<>("An error occurred while logging out", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
