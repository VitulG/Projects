package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.UserDetailsDto;
import com.scaler.paymentservice.service.paymentgateways.PaymentGatewaySelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private PaymentGatewaySelector paymentGatewaySelector;

    @Autowired
    public PaymentService(PaymentGatewaySelector paymentGatewaySelector) {
        this.paymentGatewaySelector = paymentGatewaySelector;
    }

    public String generatePaymentLink(UserDetailsDto userDetailsDto) {
        // have to add the details of api to my db
        return paymentGatewaySelector.getPaymentGateway()
                .generatePaymentLink(userDetailsDto);
    }
}
