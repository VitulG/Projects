package com.govt.irctc.service.bookingservice;

import com.govt.irctc.dto.BookingDetailsDto;
import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.exceptions.BookingExceptions.BookingCreatingException;
import com.govt.irctc.exceptions.BookingExceptions.BookingNotFoundException;
import com.govt.irctc.exceptions.CityExceptions.CityNotFoundException;
import com.govt.irctc.exceptions.CompartmentException.CompartmentNotFoundException;
import com.govt.irctc.exceptions.SeatExceptions.SeatTypeException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;

import java.util.List;

public interface BookingService {
    public String bookTickets(BookingDetailsDto bookingDetailsDto, String token) throws BookingCreatingException,
            InvalidTokenException, TokenNotFoundException, TrainNotFoundException, CityNotFoundException, CompartmentNotFoundException;
    public BookingDto getBookingByPnrNumber(Long pnr) throws BookingNotFoundException;
    public List<BookingDto> getAllUserBookings(String email) throws BookingNotFoundException,
            UserNotFoundException;
    public String cancelTickets(Long pnr) throws BookingNotFoundException;
}
