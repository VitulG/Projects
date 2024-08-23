package com.govt.irctc.service.bookingservice;

import com.govt.irctc.advice.BookingAdvice.BookingCreatingException;
import com.govt.irctc.advice.BookingAdvice.BookingNotFoundException;
import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.SeatAdvice.SeatTypeException;
import com.govt.irctc.advice.UserNotFoundException;
import com.govt.irctc.dto.BookingDetailsDto;
import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.BookingRepository;
import com.govt.irctc.repository.SeatRepository;
import com.govt.irctc.repository.TrainRepository;
import com.govt.irctc.repository.UserRepository;
import com.govt.irctc.validation.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final TokenValidation tokenValidation;
    private Long pnr;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, TrainRepository trainRepository,
                              BookingRepository bookingRepository, SeatRepository seatRepository,
                              TokenValidation tokenValidation) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.tokenValidation = tokenValidation;
        this.pnr = 1L;
    }

    @Override
    public String bookTickets(BookingDetailsDto bookingDetailsDto) throws BookingCreatingException, SeatTypeException, InvalidTokenException {
//        // user must be register to book a ticket
//
//        if(tokenValidation.isTokenValid(bookingDetailsDto.getToken())) {
//            throw new InvalidTokenException("Invalid token");
//        }
//
//        Optional<User> user = userRepository.findByUserEmail(bookingDetailsDto.getEmail());
//        if (user.isEmpty()) {
//            throw new BookingCreatingException("User must be register");
//        }
//
//        Train train = trainRepository.findByTrainNumber(bookingDetailsDto.getTrainNumber());
//        if (train == null) {
//            throw new BookingCreatingException("Train is not available");
//        }
//
//        Seats trainSeats = train.getTrainSeats().get(0);
//        if (trainSeats.getTotalNumberOfSeats() < bookingDetailsDto.getNumberOfPassengers()) {
//            throw new BookingCreatingException("Sorry, the given number of seats are not available");
//        }
//
//        // Check specific seat type availability
//        Map<SeatType, Integer> seatAvailabilityMap = Map.of(
//                SeatType.FIRST_AC, trainSeats.getOneAcSeats(),
//                SeatType.SECOND_AC, trainSeats.getTwoAcSeats(),
//                SeatType.THIRD_AC, trainSeats.getThreeAcSeats(),
//                SeatType.SLEEPER, trainSeats.getSleeperSeats(),
//                SeatType.GENERAL, trainSeats.getGeneralSeats()
//        );
//
//        Map<SeatType, Double> seatPriceMap = Map.of(
//                SeatType.FIRST_AC, trainSeats.getFirstAcPrice(),
//                SeatType.SECOND_AC, trainSeats.getSecondAcPrice(),
//                SeatType.THIRD_AC, trainSeats.getThirdAcPrice(),
//                SeatType.SLEEPER, trainSeats.getSleeperPrice(),
//                SeatType.GENERAL, trainSeats.getGeneralPrice()
//        );
//
//        SeatType seatType = null;
//
//
//        if(bookingDetailsDto.getSeatType().equals(SeatType.FIRST_AC.toString())) {
//            seatType = SeatType.FIRST_AC;
//        }else if(bookingDetailsDto.getSeatType().equals(SeatType.SECOND_AC.toString())) {
//            seatType = SeatType.SECOND_AC;
//        }else if(bookingDetailsDto.getSeatType().equals(SeatType.THIRD_AC.toString())) {
//            seatType = SeatType.THIRD_AC;
//        }else if(bookingDetailsDto.getSeatType().equals(SeatType.SLEEPER.toString())) {
//            seatType = SeatType.SLEEPER;
//        }else if(bookingDetailsDto.getSeatType().equals(SeatType.GENERAL.toString())) {
//            seatType = SeatType.GENERAL;
//        }
//
//        if(seatType == null) {
//            throw new SeatTypeException("Seat type is not available");
//        }
//
//        System.out.println(seatType);
//
//        if (seatAvailabilityMap.get(seatType) < bookingDetailsDto.getNumberOfPassengers()) {
//            throw new BookingCreatingException("Given class seats are not available");
//        }
//
//        Booking booking = new Booking();
//        booking.setPnr(pnr++);
//        booking.setBookingDate(LocalDateTime.now());
//        booking.setTrainNumber(train.getTrainNumber());
//        booking.setNumberOfPassengers(bookingDetailsDto.getNumberOfPassengers());
//        booking.setSeatType(seatType);
//        booking.setUserBookings(user.get());
//        booking.setTrains(train);
//        booking.setPaymentStatus(PaymentStatus.PENDING);
//        booking.setTicketStatus(TicketStatus.BOOKED);
//        booking.setCreatedAt(LocalDateTime.now());
//        booking.setToken(bookingDetailsDto.getToken());
//
//        double totalPrice = bookingDetailsDto.getNumberOfPassengers() * seatPriceMap.get(seatType);
//        booking.setTotalPrice(totalPrice);
//
//        // Update seats availability
//        trainSeats.setTotalNumberOfSeats(trainSeats.getTotalNumberOfSeats() - bookingDetailsDto.getNumberOfPassengers());
//        switch (seatType) {
//            case FIRST_AC:
//                trainSeats.setOneAcSeats(trainSeats.getOneAcSeats() - bookingDetailsDto.getNumberOfPassengers());
//                break;
//            case SECOND_AC:
//                trainSeats.setTwoAcSeats(trainSeats.getTwoAcSeats() - bookingDetailsDto.getNumberOfPassengers());
//                break;
//            case THIRD_AC:
//                trainSeats.setThreeAcSeats(trainSeats.getThreeAcSeats() - bookingDetailsDto.getNumberOfPassengers());
//                break;
//            case SLEEPER:
//                trainSeats.setSleeperSeats(trainSeats.getSleeperSeats() - bookingDetailsDto.getNumberOfPassengers());
//                break;
//            case GENERAL:
//                trainSeats.setGeneralSeats(trainSeats.getGeneralSeats() - bookingDetailsDto.getNumberOfPassengers());
//                break;
//            default:
//                throw new SeatTypeException("Given seat type is not available");
//        }
//
//        seatRepository.save(trainSeats);
//        train.getTrainSeats().set(0, trainSeats);
//        bookingRepository.save(booking);
//
//        return "Your ticket has been booked with id " + booking.getPnr();
        return "";
    }

    @Override
    public BookingDto getBookingByPnrNumber(Long pnr) throws BookingNotFoundException {
        Optional<Booking> booking = bookingRepository.findByPnr(pnr);
        if(booking.isEmpty()) {
            throw new BookingNotFoundException("booking does not exists with this pnr");
        }
        return booking.get().convertToBookingDto();
    }

    @Override
    public List<BookingDto> getAllUserBookings(String email) throws BookingNotFoundException, UserNotFoundException {
        Optional<User> user = userRepository.findByUserEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("user does not exists");
        }

        List<Booking> bookings = bookingRepository.findAllByUserBookings(user.get());

        if(bookings == null || bookings.isEmpty()) {
            throw new BookingNotFoundException("User did not book any ticket");
        }

        List<BookingDto> bookingDtos = new ArrayList<>();

        for(Booking booking : bookings) {
            bookingDtos.add(booking.convertToBookingDto());
        }
        return bookingDtos;
    }


    @Override
    public String cancelTickets(Long pnr) throws BookingNotFoundException {
        Optional<Booking> booking = bookingRepository.findByPnr(pnr);
        if(booking.isEmpty()) {
            throw new BookingNotFoundException("Booking is not exists");
        }

        Booking existingBooking = booking.get();

        existingBooking.setUpdatedAt(LocalDateTime.now());
        existingBooking.setDeleted(true);
        existingBooking.setTicketStatus(TicketStatus.CANCELLED);
        existingBooking.setPaymentStatus(PaymentStatus.CANCELLED);

        Seats trainSeats  = existingBooking.getTrains().getTrainSeats().get(0);
        trainSeats.setTotalNumberOfSeats(trainSeats.getTotalNumberOfSeats() + existingBooking.getNumberOfPassengers());
        bookingRepository.save(existingBooking);

        switch (existingBooking.getSeatType()) {
            case FIRST_AC:
                trainSeats.setOneAcSeats(trainSeats.getOneAcSeats() + existingBooking.getNumberOfPassengers());
                break;
            case SECOND_AC:
                trainSeats.setTwoAcSeats(trainSeats.getTwoAcSeats() + existingBooking.getNumberOfPassengers());
                break;
            case THIRD_AC:
                trainSeats.setThreeAcSeats(trainSeats.getThreeAcSeats() + existingBooking.getNumberOfPassengers());
                break;
            case SLEEPER:
                trainSeats.setSleeperSeats(trainSeats.getSleeperSeats() + existingBooking.getNumberOfPassengers());
                break;
            case GENERAL:
                trainSeats.setGeneralSeats(trainSeats.getGeneralSeats() + existingBooking.getNumberOfPassengers());
                break;
        }
        seatRepository.save(trainSeats);
        return "your ticket has been cancelled";
    }
}
