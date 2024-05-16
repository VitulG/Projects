package com.scaler.paymentservice.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {

    @RequestMapping(value = "/stripe/webhook", method = RequestMethod.POST)
    public void getStripeResponse(@RequestBody Object obj) {
        System.out.println(obj);
    }
}
