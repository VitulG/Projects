package com.govt.irctc.service.bookingservice;

import com.govt.irctc.dto.BookingDetailsDto;
import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.enums.*;
import com.govt.irctc.exceptions.BookingExceptions.BookingCreatingException;
import com.govt.irctc.exceptions.BookingExceptions.BookingNotFoundException;
import com.govt.irctc.exceptions.CityExceptions.CityNotFoundException;
import com.govt.irctc.exceptions.CompartmentException.CompartmentNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.exceptions.UserExceptions.UserNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.*;
import com.govt.irctc.strategy.DistanceCalculationStrategy;
import com.govt.irctc.strategy.FareCalculationStrategy;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final TokenRepository tokenRepository;
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final CompartmentRepository compartmentRepository;
    private final FareCalculationStrategy fareCalculationStrategy;
    private final CityRepository cityRepository;
    private final DistanceCalculationStrategy distanceCalculationStrategy;


    @Autowired
    public BookingServiceImpl(TokenRepository tokenRepository, TrainRepository trainRepository,
                              BookingRepository bookingRepository, SeatRepository seatRepository, CompartmentRepository compartmentRepository, FareCalculationStrategy fareCalculationStrategy, CityRepository cityRepository, DistanceCalculationStrategy distanceCalculationStrategy) {
        this.bookingRepository = bookingRepository;
        this.trainRepository = trainRepository;
        this.tokenRepository = tokenRepository;
        this.seatRepository = seatRepository;
        this.compartmentRepository = compartmentRepository;
        this.fareCalculationStrategy = fareCalculationStrategy;
        this.cityRepository = cityRepository;
        this.distanceCalculationStrategy = distanceCalculationStrategy;
    }

    @Override
    @Transactional
    public String bookTickets(BookingDetailsDto bookingDetailsDto, String token) throws BookingCreatingException, InvalidTokenException, TokenNotFoundException, TrainNotFoundException, CityNotFoundException, CompartmentNotFoundException {
        Token existingToken = getAndValidateToken(token);
        User user = existingToken.getUserTokens();

        // validate the booking details here
        Train train = trainRepository.findByTrainNumber(bookingDetailsDto.getTrainNumber())
                .orElseThrow(() -> new TrainNotFoundException("Train not found with this number: "+bookingDetailsDto.getTrainNumber()));

        CompartmentType compartmentType = CompartmentType.valueOf(bookingDetailsDto.getCompartmentType().toUpperCase());

        List<Seat> availableSeats = findAvailableSeats(train, compartmentType, bookingDetailsDto.getNumberOfPassengers());
        allocateAvailableSeatsForUser(user, availableSeats, bookingDetailsDto.getNumberOfPassengers());

        City src = cityRepository.findByCityName(bookingDetailsDto.getFrom())
                .orElseThrow(() -> new CityNotFoundException("Source city not found"));
        City dest = cityRepository.findByCityName(bookingDetailsDto.getTo())
                .orElseThrow(() -> new CityNotFoundException("Destination city not found"));

        validateStations(train, src, dest);

        Booking booking = createBooking(bookingDetailsDto, user, train, src, dest, availableSeats.size(), availableSeats);
        processPayments(booking, bookingDetailsDto.getPaymentMethods());
        bookingRepository.save(booking);

        for(Seat seat : booking.getBookedSeats()) {
            seat.setBooking(booking);
        }

        return "your ticket(s) has been booked with pnr: "+booking.getPnr();
    }

    private String generatePnr() {
        return UUID.randomUUID().toString().replace("-","").substring(0,6);
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Token is expired or deleted");
        }
        return existingToken;
    }

    private void validateStations(Train train, City src, City dest) throws BookingCreatingException {
        if(!train.getStations().contains(src.getStation())) {
            throw new BookingCreatingException("source station not found");
        }

        if(!train.getStations().contains(dest.getStation())) {
            throw new BookingCreatingException("destination station not found");
        }
    }

    private List<Seat> findAvailableSeats(Train train, CompartmentType compartmentType, int numberOfPassengers) throws BookingCreatingException, CompartmentNotFoundException {
        List<Compartment> compartments = compartmentRepository.findAllByTrainAndCompartmentType(train, compartmentType);

        if(compartments.isEmpty()) {
            throw new CompartmentNotFoundException("Compartment not found for the train: "+train.getTrainNumber()+
                    " with compartment type: "+compartmentType);
        }

        for(Compartment compartment : compartments) {
            List<Seat> compartmentAvailableSeats = seatRepository
                    .findAllByCompartmentAndSeatStatus(compartment, SeatStatus.AVAILABLE);

            if(compartmentAvailableSeats.size() >= numberOfPassengers) {
                return compartmentAvailableSeats.subList(0, numberOfPassengers);
            }

        }
        throw new BookingCreatingException("Not enough available seats in any compartment of this type: "+compartmentType);
    }

    private void allocateAvailableSeatsForUser(User user, List<Seat> availableSeats, int numberOfPassengers) {
        for (int i = 0; i < numberOfPassengers; i++) {
            Seat seat = availableSeats.get(i);
            seat.setSeatStatus(SeatStatus.BOOKED);
            seatRepository.save(seat);
        }
    }

    private Booking createBooking(BookingDetailsDto bookingDetailsDto, User user, Train train, City src,
                                  City dest, int numberOfPassengers, List<Seat> bookedSeats) {
        Booking booking = new Booking();
        booking.setPnr(generatePnr());
        booking.setBookingDate(LocalDateTime.now());
        booking.setNumberOfPassengers(bookingDetailsDto.getNumberOfPassengers());
        booking.setBookedSeats(bookedSeats);

        double totalDistance = distanceCalculationStrategy.calculateDistance(src.getLatitude(), src.getLongitude(),
                dest.getLatitude(), dest.getLongitude());

        double totalFare = fareCalculationStrategy.calculateFare(CompartmentType.valueOf(bookingDetailsDto
                        .getCompartmentType()),
                totalDistance);

        booking.setTotalPrice(totalFare);
        booking.setCompartmentType(CompartmentType.valueOf(bookingDetailsDto.getCompartmentType()));

        booking.setUserBookings(user);

        booking.setTrains(train);
        booking.setTicketStatus(TicketStatus.BOOKED);
        user.getUserBookings().add(booking);

        return booking;
    }
    private void processPayments(Booking booking, List<String> paymentMethods) throws BookingCreatingException {
        List<Payment> payments = new ArrayList<>();

        for(String paymentMethod : paymentMethods) {
            Payment payment = new Payment();
            payment.setRefundStatus(RefundStatus.NA);
            payment.setMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setBooking(booking);
            payments.add(payment);
        }
        booking.setPayments(payments);
    }

    @Override
    public BookingDto getBookingByPnrNumber(Long pnr) throws BookingNotFoundException {
//        Optional<Booking> booking = Optional.empty();
//        if(booking.isEmpty()) {
//            throw new BookingNotFoundException("booking does not exists with this pnr");
        return null;
    }

    @Override
    public List<BookingDto> getAllUserBookings(String email) throws BookingNotFoundException, UserNotFoundException {
//        Optional<User> user = userRepository.findByUserEmail(email);
//        if(user.isEmpty()) {
//            throw new UserNotFoundException("user does not exists");
//        }
//
//        List<Booking> bookings = bookingRepository.findAllByUserBookings(user.get());
//
//        if(bookings == null || bookings.isEmpty()) {
//            throw new BookingNotFoundException("User did not book any ticket");
//        }
//
//        List<BookingDto> bookingDtos = new ArrayList<>();
//
//        for(Booking booking : bookings) {
//
//        }
//        return bookingDtos;
        return List.of();
    }


    @Override
    public String cancelTickets(Long pnr) throws BookingNotFoundException {
//        Optional<Booking> booking = Optional.empty();
//        if(booking.isEmpty()) {
//            throw new BookingNotFoundException("Booking is not exists");
//        }
//
//        Booking existingBooking = booking.get();
//
//        existingBooking.setUpdatedAt(LocalDateTime.now());
//        existingBooking.setDeleted(true);
//        existingBooking.setTicketStatus(TicketStatus.CANCELLED);
//        existingBooking.setPaymentStatus(PaymentStatus.CANCELLED);

//        Seats trainSeats  = existingBooking.getTrains().getTrainSeats().get(0);
//        trainSeats.setTotalNumberOfSeats(trainSeats.getTotalNumberOfSeats() + existingBooking.getNumberOfPassengers());
//        bookingRepository.save(existingBooking);
//
//        switch (existingBooking.getSeatType()) {
//            case FIRST_AC:
//                trainSeats.setOneAcSeats(trainSeats.getOneAcSeats() + existingBooking.getNumberOfPassengers());
//                break;
//            case SECOND_AC:
//                trainSeats.setTwoAcSeats(trainSeats.getTwoAcSeats() + existingBooking.getNumberOfPassengers());
//                break;
//            case THIRD_AC:
//                trainSeats.setThreeAcSeats(trainSeats.getThreeAcSeats() + existingBooking.getNumberOfPassengers());
//                break;
//            case SLEEPER:
//                trainSeats.setSleeperSeats(trainSeats.getSleeperSeats() + existingBooking.getNumberOfPassengers());
//                break;
//            case GENERAL:
//                trainSeats.setGeneralSeats(trainSeats.getGeneralSeats() + existingBooking.getNumberOfPassengers());
//                break;
//        }
//        seatRepository.save(trainSeats);
        return "your ticket has been cancelled";
    }
}
