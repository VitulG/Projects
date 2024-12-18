package com.govt.irctc.model;

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
}