package com.govt.irctc.service.paymentservice.paymentgateways;

import com.govt.irctc.advice.PaymentAdvice.PaymentLinkGenerationException;
import com.govt.irctc.model.Booking;
import com.govt.irctc.model.User;

public interface PaymentGateway {
    String generatePaymentLink(Booking booking) throws PaymentLinkGenerationException;
}
