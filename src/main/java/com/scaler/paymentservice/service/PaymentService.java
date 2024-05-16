package com.scaler.paymentservice.service;

import com.scaler.paymentservice.dto.UserDetailsDto;
import com.scaler.paymentservice.model.User;
import com.scaler.paymentservice.repository.UserRepository;
import com.scaler.paymentservice.service.paymentgateways.PaymentGatewaySelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private PaymentGatewaySelector paymentGatewaySelector;
    private UserRepository userRepository;

    @Autowired
    public PaymentService(PaymentGatewaySelector paymentGatewaySelector,
                          UserRepository userRepository) {
        this.paymentGatewaySelector = paymentGatewaySelector;
        this.userRepository = userRepository;
    }

    public String generatePaymentLink(UserDetailsDto userDetailsDto) {
        // have to add the details of api to my db
        User user = new User();
        user.setUserName(userDetailsDto.getUserName());
        user.setEmail(userDetailsDto.getEmail());
        user.setAmount(userDetailsDto.getAmount());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setOrderId(userDetailsDto.getOrderId());

        userRepository.save(user);

        return paymentGatewaySelector.getPaymentGateway()
                .generatePaymentLink(userDetailsDto);
    }
}
