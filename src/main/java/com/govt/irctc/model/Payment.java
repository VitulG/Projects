package com.govt.irctc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.govt.irctc.enums.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment extends BaseModel{
    private String transactionNumber; // uuid for each transaction

    @ManyToOne
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
}
