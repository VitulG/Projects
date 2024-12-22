package com.govt.irctc.UserControllerTest;

import com.govt.irctc.controller.UserController;
import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.dto.UserDto;
import com.govt.irctc.dto.UserUpdateDetailsDto;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;
import com.govt.irctc.exceptions.UserExceptions.UserUpdationException;
import com.govt.irctc.service.userService.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @Test
    public void testGetUserByEmail() throws UserNotFoundException, InvalidTokenException,
            UnauthorizedUserException {
        // Arrange
        String email = "guptavitul@gmail.com";
        String token = "abc";
        UserDto user = new UserDto();
        user.setUserEmail(email);

        when(userService.getUserByEmail(email, token)).
                thenReturn(user);

        // Act
        ResponseEntity<UserDto> response = userController.getUserByEmail(email, token);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("guptavitul@gmail.com", Objects.requireNonNull(response.getBody()).getUserEmail());
    }

    @Test
    public void testGetAllUsers() throws InvalidTokenException, UnauthorizedUserException {
        UserDto user1 = new UserDto();
        UserDto user2 = new UserDto();

        List<UserDto> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);

        when(userService.getAllUsers("")).thenReturn(list);

        ResponseEntity<List<UserDto>> response = userController.getAllUsers("");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());

    }

    @Test
    public void testUpdateUserByEmailId() throws UserNotFoundException, InvalidTokenException, UnauthorizedUserException,
            UserUpdationException {
        UserDto existingUser = new UserDto();
        existingUser.setUserEmail("guptavitul@gmail.com");
        String email = "guptavitul@gmail.com";
        String token = "abc";

        when(userService.getUserByEmail(email, token)).thenReturn(existingUser);

        UserUpdateDetailsDto updateDetailsDto = new UserUpdateDetailsDto();
        updateDetailsDto.setUpdatedEmail("rahul@gmail.com");

        when(userService.updateUserById(token, updateDetailsDto)).thenReturn("updated");

        ResponseEntity<String> response = userController.updateUserById(updateDetailsDto, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updated", response.getBody());
    }

    @Test
    public void testDeleteUserByEmailId() throws UserNotFoundException, InvalidTokenException,
            UnauthorizedUserException, TokenNotFoundException {
        String email = "guptavitul@gmail.com";

        UserDto user = new UserDto();
        String token = "abc";
        user.setUserEmail(email);

        when(userService.getUserByEmail(email, token)).thenReturn(user);
        when(userService.deleteUserById(email, "")).thenReturn("deleted");

        ResponseEntity<String> response = userController.deleteUserById(email, "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("deleted", response.getBody());
    }

    @Test
    public void testUserBookings() throws UserNotFoundException, InvalidTokenException,
            UnauthorizedUserException, TokenNotFoundException {
        UserDto user = new UserDto();
        user.setUserEmail("guptavitul@gmail.com");

        when(userService.getUserByEmail("guptavitul@gmail.com", "")).thenReturn(user);

        List<BookingDto> bookings = new ArrayList<>();
        BookingDto booking1 = new BookingDto();
        BookingDto booking2 = new BookingDto();

        bookings.add(booking1);
        bookings.add(booking2);

        when(userService.getUserBookings("guptavitul@gmail.com", "")).thenReturn(bookings);

        ResponseEntity<List<BookingDto>> response = userController.getUserBookings("guptavitul@gmail.com", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());

    }
}
