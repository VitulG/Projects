package com.govt.irctc.service.bookingservice;

import com.govt.irctc.advice.BookingAdvice.BookingCreatingException;
import com.govt.irctc.advice.BookingAdvice.BookingNotFoundException;
import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.SeatAdvice.SeatTypeException;
import com.govt.irctc.advice.UserNotFoundException;
import com.govt.irctc.dto.BookingDetailsDto;
import com.govt.irctc.dto.BookingDto;

import java.util.List;

public interface BookingService {
    public String bookTickets(BookingDetailsDto bookingDetailsDto) throws BookingCreatingException,
            SeatTypeException, InvalidTokenException;
    public BookingDto getBookingByPnrNumber(Long pnr) throws BookingNotFoundException;
    public List<BookingDto> getAllUserBookings(String email) throws BookingNotFoundException,
            UserNotFoundException;
    public String cancelTickets(Long pnr) throws BookingNotFoundException;
}
