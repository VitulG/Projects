package com.scaler.paymentservice.service.paymentgateways;

import com.scaler.paymentservice.dto.UserDetailsDto;
import org.springframework.stereotype.Service;

@Service
public class StripeGateway implements PaymentGateway {
    @Override
    public String generatePaymentLink(UserDetailsDto userDetailsDto) {
        return "";
    }
}
