package com.govt.irctc.controller;

import com.govt.irctc.dto.*;
import com.govt.irctc.exceptions.SecurityExceptions.*;
import com.govt.irctc.exceptions.UserExceptions.*;
import com.govt.irctc.service.userService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // logger
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDetailsDto loginDetailsDto) {
        try {
            LoginResponseDto response = userService.getAndValidateUser(loginDetailsDto);

            if(response == null) {
                throw new InvalidCredentialsException("Invalid user name/password");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidCredentialsException | PasswordMismatchException exception) {
            exception.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/logout/{token}")
    public ResponseEntity<String> logout(@PathVariable("token") String token) {
        try {
            String message = userService.logoutUser(token);
            if(message == null || message.isEmpty()){
                throw new TokenNotFoundException("token not found");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (TokenNotFoundException exception) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserDto> validateToken(@RequestHeader("Authorization")
                                                     String token) {
        try {
            UserDto user = userService.validateUserToken(token);
            if(user == null) {
                throw new InvalidTokenException("token is not valid");
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NullPointerException | TokenNotFoundException | InvalidTokenException exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/get-user/{email}", method = RequestMethod.GET)
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email,
                                                  @RequestHeader("Authorization") String token) {
        try {
            UserDto userDto = userService.getUserByEmail(email, token);
            if(userDto == null) {
                throw new UserNotFoundException("user not found");
            }
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }catch (UserNotFoundException userNotFound) {
            //add logger here
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }catch(InvalidTokenException invalidTokenException) {
            // logger
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }catch(UnauthorizedUserException unauthorizedUserException) {
            //logger
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }catch(Exception ex) {
            // logger
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            List<UserDto> allUsers = userService.getAllUsers(token);
            if(allUsers.isEmpty()) {
                throw new UserNotFoundException("users not found");
            }
            return new ResponseEntity<>(allUsers, HttpStatus.OK);
        }catch (InvalidTokenException | UnauthorizedUserException userException) {
            userException.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }catch (UserNotFoundException userNotFound) {
            userNotFound.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/update-user/{email}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateUserById(@PathVariable("email") String email,
                                                 @RequestBody UserDto userDto,
                                                 @RequestHeader("Authorization") String token) {
        try {
            String message = userService.updateUserById(email, userDto, token);
            if(message == null || message.isEmpty()) {
                throw new UserUpdationException("unable to update the user");
            }
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch ( UnauthorizedUserException | InvalidTokenException userOperation) {
            userOperation.printStackTrace();
            return new ResponseEntity<>("user is unauthorized to update", HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException userException) {
            userException.printStackTrace();
            return new ResponseEntity<>(userException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserUpdationException userUpdationException) {
            userUpdationException.printStackTrace();
            return new ResponseEntity<>(userUpdationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
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
