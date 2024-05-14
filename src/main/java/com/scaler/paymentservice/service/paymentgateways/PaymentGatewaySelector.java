package com.scaler.paymentservice.service.paymentgateways;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewaySelector {
    private RazorpayGateway razorpayGateway;
    private StripeGateway stripeGateway;

    @Autowired
    public PaymentGatewaySelector(RazorpayGateway razorpayGateway,
                                  StripeGateway stripeGateway) {
        this.razorpayGateway = razorpayGateway;
        this.stripeGateway = stripeGateway;
    }

    public PaymentGateway getPaymentGateway() {
        // logic to choose the best payment gateway
        return razorpayGateway;
    }
}
