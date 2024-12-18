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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String transactionId;

    @OneToOne
    private Booking booking;

    private PaymentStatus status;
    private RefundStatus refundStatus;
    private String token;

}
