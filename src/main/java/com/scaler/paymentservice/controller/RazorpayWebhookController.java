package com.scaler.paymentservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RazorpayWebhookController {

    @PostMapping("/razorpayWebhook")
    public void paymentDone(@RequestBody Object object) {
        System.out.println(object);
        // update the payment db here you basically send the dto
    }
}
