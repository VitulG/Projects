package com.govt.irctc.controller;

import com.govt.irctc.dto.*;
import com.govt.irctc.exceptions.SecurityExceptions.*;
import com.govt.irctc.exceptions.UserExceptions.*;
import com.govt.irctc.service.userService.UserService;
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

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<String> signUpUser(@RequestBody UserSignupDetailsDto userSignupDetailsDto) {
        try {
            String message = userService.addUser(userSignupDetailsDto);
            if(message == null || message.isEmpty()){
                throw new UserCreationException("Unable to create user");
            }
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch(UserCreationException | UserAlreadyExistsException userException) {
            return new ResponseEntity<>(userException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDetailsDto loginDetailsDto) {
        LoginResponseDto response;
        try {
            response = userService.getAndValidateUser(loginDetailsDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidCredentialsException | PasswordMismatchException | LoginValidationException exception) {
            response = new LoginResponseDto();
            response.setErrorMessage(exception.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            response = new LoginResponseDto();
            response.setErrorMessage(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity<String> logout(@PathVariable("token") String token) {
        try {
            String message = userService.logoutUser(token);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (TokenNotFoundException exception) {
            return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
        }catch (Exception ex) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validate-user-token")
    public ResponseEntity<UserDto> validateToken(@RequestHeader("Authorization") String token) {
        UserDto user;
        try {
            user = userService.validateUserToken(token);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (TokenNotFoundException | InvalidTokenException exception) {
            user = new UserDto();
            user.setErrorMessage(exception.getMessage());
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            user = new UserDto();
            user.setErrorMessage(ex.getMessage());
            return new ResponseEntity<>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/get-user/{email}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email,
                                                  @RequestHeader("Authorization") String token) {
        UserDto userDto;
        try {
            userDto = userService.getUserByEmail(email, token);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }catch (UserNotFoundException userNotFound) {
            userDto = new UserDto();
            userDto.setErrorMessage(userNotFound.getMessage());
            return new ResponseEntity<>(userDto, HttpStatus.NOT_FOUND);
        }catch(InvalidTokenException | UnauthorizedUserException securityException) {
            userDto = new UserDto();
            userDto.setErrorMessage(securityException.getMessage());
            return new ResponseEntity<>(userDto, HttpStatus.UNAUTHORIZED);
        }catch(Exception ex) {
            userDto = new UserDto();
            userDto.setErrorMessage(ex.getMessage());
            return new ResponseEntity<>(userDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        List<UserDto> allUsers;
        try {
            allUsers = userService.getAllUsers(token);
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }catch (InvalidTokenException | UnauthorizedUserException userException) {
            allUsers = new ArrayList<>();
            return new ResponseEntity<>(allUsers, HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            allUsers = new ArrayList<>();
            return new ResponseEntity<>(allUsers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update-user", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUserById(@RequestBody UserUpdateDetailsDto updateDetailsDto,
                                                 @RequestHeader("Authorization") String token) {
        try {
            String message = userService.updateUserById(token, updateDetailsDto);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch ( UnauthorizedUserException | InvalidTokenException securityException) {
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserUpdationException userUpdationException) {
            return new ResponseEntity<>(userUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-user/{email}")
    public ResponseEntity<String> deleteUserById(@PathVariable("email") String email,
                                                 @RequestHeader("Authorization") String token) {
        try {
            String message = userService.deleteUserById(email, token);
            if(message == null || message.isEmpty()) {
                throw new UserDeletionException("unable to delete the user");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (UserNotFoundException userNotFoundException) {
            userNotFoundException.printStackTrace();
            return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        }catch (UnauthorizedUserException | TokenNotFoundException | InvalidTokenException userException) {
            userException.printStackTrace();
            return new ResponseEntity<>(userException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserDeletionException userDeletionException) {
            userDeletionException.printStackTrace();
            return new ResponseEntity<>(userDeletionException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-user-bookings/{email}")
    public ResponseEntity<List<BookingDto>> getUserBookings(@PathVariable("email") String email,
                                                            @RequestHeader("Authorization") String token) {
        try {
            List<BookingDto> userBookings = userService.getUserBookings(email, token);
            if(userBookings == null) {
                throw new UserBookingsNotFoundException("user not found");
            }
            return new ResponseEntity<>(userBookings, HttpStatus.OK);
        }catch (UserNotFoundException | UserBookingsNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (UnauthorizedUserException | InvalidTokenException exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
