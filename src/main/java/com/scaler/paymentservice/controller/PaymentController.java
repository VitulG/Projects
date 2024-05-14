package com.scaler.paymentservice.controller;

import com.scaler.paymentservice.dto.UserDetailsDto;
import com.scaler.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> initiatePayment(@RequestBody UserDetailsDto userDetailsDto) {
        String response = paymentService.generatePaymentLink(userDetailsDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
