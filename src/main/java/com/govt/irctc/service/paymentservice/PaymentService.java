package com.govt.irctc.service.paymentservice;

import com.govt.irctc.advice.BookingAdvice.BookingNotFoundException;
import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.PaymentAdvice.PaymentLinkGenerationException;
import com.govt.irctc.advice.PaymentAdvice.PaymentNotFoundException;

public interface PaymentService {
    String initiatePayment(Long pnr) throws BookingNotFoundException, PaymentLinkGenerationException, InvalidTokenException;
    String getPaymentStatus(Long transactionId) throws PaymentNotFoundException;
    String getPaymentRefundStatus(Long transactionId) throws PaymentNotFoundException;
}
