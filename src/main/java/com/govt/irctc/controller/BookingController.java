package com.govt.irctc.controller;

import com.govt.irctc.advice.BookingAdvice.BookingCreatingException;
import com.govt.irctc.advice.BookingAdvice.BookingNotFoundException;
import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.SeatAdvice.SeatTypeException;
import com.govt.irctc.advice.UserNotFoundException;
import com.govt.irctc.dto.BookingDetailsDto;
import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.service.bookingservice.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @RequestMapping(value  = "/book-ticket", method = RequestMethod.POST)
    public ResponseEntity<String> bookTickets(@RequestBody BookingDetailsDto bookingDetailsDto) {
        try {
            String message = bookingService.bookTickets(bookingDetailsDto);
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        } catch (BookingCreatingException | SeatTypeException | InvalidTokenException bookingCreatingException) {
            bookingCreatingException.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(value = "/get-booking/{pnr}", method = RequestMethod.GET)
    public ResponseEntity<BookingDto> getBookingByPnr(@PathVariable("pnr") Long pnr) {
        try {
            BookingDto booking = bookingService.getBookingByPnrNumber(pnr);
            if(booking == null) {
                throw  new BookingNotFoundException("PNR is not exists");
            }
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }catch (BookingNotFoundException bookingNotFound) {
            bookingNotFound.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/get-all-user-bookings/{email}", method = RequestMethod.GET)
    public ResponseEntity<List<BookingDto>> getAllBooingByEmail(@PathVariable("email") String email) {
        try {
            List<BookingDto> bookings = bookingService.getAllUserBookings(email);
            if(bookings == null) {
                throw new BookingNotFoundException("booking does not exists");
            }
            return  new ResponseEntity<>(bookings, HttpStatus.OK);
        }catch (UserNotFoundException | BookingNotFoundException exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value="/cancel-booking/{pnr}", method = RequestMethod.DELETE)
    public ResponseEntity<String> cancelTickets(@PathVariable("pnr") Long pnr) {
        try {
            String message = bookingService.cancelTickets(pnr);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }catch (BookingNotFoundException bookingNotFound) {
            bookingNotFound.printStackTrace();
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
