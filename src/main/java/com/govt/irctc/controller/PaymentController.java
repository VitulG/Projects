package com.govt.irctc.controller;

import com.govt.irctc.exceptions.BookingExceptions.BookingNotFoundException;

import com.govt.irctc.exceptions.PaymentExceptions.PaymentLinkGenerationException;
import com.govt.irctc.exceptions.PaymentExceptions.PaymentNotFoundException;
import com.govt.irctc.service.paymentservice.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate-payment/{pnr}")
    public ResponseEntity<String> initiatePayment(@PathVariable("pnr") Long pnr) {
        try {
            String message = paymentService.initiatePayment(pnr);
            if(message == null || message.isEmpty()) {
                throw new BookingNotFoundException("booking PNR doesn't exists");
            }
            return new ResponseEntity<>(message, HttpStatus.CREATED);
        }catch (BookingNotFoundException | PaymentLinkGenerationException |
                com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException exception) {
            exception.printStackTrace();
        }
        return new ResponseEntity<>("booking PNR doesn't exists", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/initiate-refund/{pnr}")
    public ResponseEntity<String> initiateRefund(@PathVariable("pnr") Long pnr) {
        return null;
    }

    // check status for payment and refund
    @GetMapping("/payment-status/{transactionId}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable("transactionId") Long transactionId) {
        try {
            String status = paymentService.getPaymentStatus(transactionId);

            if(status == null) {
                throw new PaymentNotFoundException("payment not found");
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        }catch (PaymentNotFoundException paymentNotFoundException) {
            paymentNotFoundException.printStackTrace();
        }
        return new ResponseEntity<>("payment not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/payment-refund-status/{transactionId}")
    public ResponseEntity<String> getPaymentRefundStatus(@PathVariable("transactionId") Long transactionId) {
        try {
            String status = paymentService.getPaymentRefundStatus(transactionId);

            if(status == null) {
                throw new PaymentNotFoundException("payment not found");
            }
            return new ResponseEntity<>(status, HttpStatus.OK);
        }catch (PaymentNotFoundException paymentNotFoundException) {
            paymentNotFoundException.printStackTrace();
        }
        return new ResponseEntity<>("payment not found", HttpStatus.NOT_FOUND);
    }
}
