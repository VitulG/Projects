package com.scaler.paymentservice.service.paymentgateways;

import com.scaler.paymentservice.dto.UserDetailsDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;
import com.stripe.param.PaymentLinkCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeGateway implements PaymentGateway {
    @Override
    public String generatePaymentLink(UserDetailsDto userDetailsDto) {
        Stripe.apiKey = "sk_test_51PG3nKSB9TWehVh24DkoXDbCPE2lOOS3LY8AMsSgnwGjzrYSpQLmz4UONM9sKYvTf51bkSbzXQTPMZBTZhliIYMh00Z3GZJWc5";
        try {
            PaymentLinkCreateParams params =
                    PaymentLinkCreateParams.builder()
                            .addLineItem(
                                    PaymentLinkCreateParams.LineItem.builder()
                                            .setPrice("50000") // create a price class for this
                                            .setQuantity(userDetailsDto.getQuantity())
                                            .build()
                            )
                            .build();

            PaymentLink paymentLink = PaymentLink.create(params);
            return paymentLink.getUrl();
        }catch(StripeException se) {
            System.out.println("Message: " + se.getMessage());
        }
        return "";
    }
}
