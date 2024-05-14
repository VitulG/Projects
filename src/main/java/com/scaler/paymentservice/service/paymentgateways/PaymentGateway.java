package com.scaler.paymentservice.service.paymentgateways;

import com.scaler.paymentservice.dto.UserDetailsDto;

public interface PaymentGateway {
    public String generatePaymentLink(UserDetailsDto userDetailsDto);
}
