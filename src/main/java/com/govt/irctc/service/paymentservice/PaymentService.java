package com.govt.irctc.service.paymentservice;


import com.govt.irctc.exceptions.BookingExceptions.BookingNotFoundException;
import com.govt.irctc.exceptions.PaymentExceptions.PaymentLinkGenerationException;
import com.govt.irctc.exceptions.PaymentExceptions.PaymentNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;

public interface PaymentService {
    String initiatePayment(Long pnr) throws BookingNotFoundException, PaymentLinkGenerationException, InvalidTokenException;
    String getPaymentStatus(Long transactionId) throws PaymentNotFoundException;
    String getPaymentRefundStatus(Long transactionId) throws PaymentNotFoundException;
}
