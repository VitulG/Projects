package com.scaler.paymentservice.controller;

import com.scaler.paymentservice.dto.UserDetailsDto;
import com.scaler.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripePaymentController {
    private PaymentService paymentService;

    @Autowired
    public StripePaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    @ResponseBody
    public ResponseEntity<String> generatePaymentLink(@RequestBody UserDetailsDto userDetailsDto) {
        String response = paymentService.generatePaymentLink(userDetailsDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
