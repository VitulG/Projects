package com.govt.irctc.model;

import com.govt.irctc.dto.BookingDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pnr;
    private LocalDateTime bookingDate;
    private Long trainNumber;
    private int numberOfPassengers;
    private double totalPrice;
    private SeatType seatType;
    private String token;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User userBookings;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Train trains;

    @OneToOne
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public BookingDto convertToBookingDto() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setPnr(pnr);
        bookingDto.setBookingDate(bookingDate);
        bookingDto.setName(userBookings.getUserName());
        bookingDto.setEmail(userBookings.getUserEmail());
        bookingDto.setTrainNumber(trains.getTrainNumber());
        bookingDto.setNumberOfPassengers(numberOfPassengers);
        bookingDto.setTicketStatus(ticketStatus.toString());
        bookingDto.setSeatType(seatType.toString());
        bookingDto.setTotalPrice(totalPrice);
        bookingDto.setPaymentStatus(paymentStatus.toString());
        return bookingDto;
    }
}