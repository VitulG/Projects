package com.scaler.paymentservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RazorpayWebhookController {

    @PostMapping("/razorpay/webhook")
    public ResponseEntity<String> handleWebhookEvent(@RequestBody String payload) {
        System.out.println(payload);
        return new ResponseEntity<>("Received", HttpStatus.OK);
    }
}
