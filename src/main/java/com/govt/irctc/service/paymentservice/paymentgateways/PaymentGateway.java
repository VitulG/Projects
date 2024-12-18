package com.govt.irctc.service.paymentservice.paymentgateways;

import com.govt.irctc.exceptions.PaymentExceptions.PaymentLinkGenerationException;
import com.govt.irctc.model.Booking;

public interface PaymentGateway {
    String generatePaymentLink(Booking booking) throws PaymentLinkGenerationException;
}
