package com.scaler.paymentservice.service.paymentgateways;

import com.razorpay.*;
import com.scaler.paymentservice.dto.UserDetailsDto;
import com.scaler.paymentservice.entity.User;
import com.scaler.paymentservice.repository.UserRepository;
import org.json.JSONObject;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RazorpayGateway implements PaymentGateway {

    private UserRepository userRepository;

    @Autowired
    public RazorpayGateway(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String generatePaymentLink(UserDetailsDto userDetailsDto) {

        try {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_MZEtxncR4Vkqcn", "UYNI98uIc9BIlHddTmzrCl9N");

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", userDetailsDto.getAmount()*100);
            paymentLinkRequest.put("currency","INR");
            paymentLinkRequest.put("accept_partial",false);
            paymentLinkRequest.put("expire_by",1715815996);
            paymentLinkRequest.put("reference_id", userDetailsDto.getOrderId().toString());
            paymentLinkRequest.put("description","Payment for policy no #23456");

            JSONObject customer = new JSONObject();
            customer.put("name", userDetailsDto.getUserName());
            customer.put("contact", userDetailsDto.getPhoneNumber().toString());
            customer.put("email", userDetailsDto.getEmail());
            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);
            paymentLinkRequest.put("reminder_enable",true);

            JSONObject notes = new JSONObject();
            notes.put("policy_name","Jeevan Bima");
            paymentLinkRequest.put("notes",notes);

            paymentLinkRequest.put("callback_url","https://google.com/");
            paymentLinkRequest.put("callback_method","get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);
            return payment.toString();
        }catch(Exception ex) {
            ex.printStackTrace();
        }

        // saving the users details in user table
        User user = new User();

        user.setUserName(userDetailsDto.getUserName());
        user.setOrderId(userDetailsDto.getOrderId());
        user.setAmount(userDetailsDto.getAmount());
        user.setPhoneNumber(userDetailsDto.getPhoneNumber());
        user.setEmail(userDetailsDto.getEmail());

        userRepository.save(user);

        return "";
    }
}
