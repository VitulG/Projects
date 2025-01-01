package com.govt.irctc.model;

import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.enums.CompartmentType;
import com.govt.irctc.enums.PaymentStatus;
import com.govt.irctc.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseModel{

    private String pnr; // will generate a unique UUID for each booking
    private LocalDateTime bookingDate;

    private int numberOfPassengers;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private CompartmentType compartmentType;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User userBookings;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Train trains;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy ="booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Seat> bookedSeats;

    public BookingDto convertToBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setTrainNumber(this.trains.getTrainNumber());
        bookingDto.setPnr(pnr);
        bookingDto.setBookingDate(bookingDate);
        bookingDto.setCompartmentType(compartmentType.toString().toLowerCase());
        bookingDto.setTicketStatus(ticketStatus.toString().toLowerCase());
        bookingDto.setPaymentStatus(paymentStatus.toString().toLowerCase());
        bookingDto.setTotalPrice(totalPrice);
        bookingDto.setBookedSeats(getBookedSeats()
                .stream()
                .map(Seat::convertToSeatDto)
                .toList());
        return bookingDto;
    }
}