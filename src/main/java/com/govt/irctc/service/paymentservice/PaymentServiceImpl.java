package com.govt.irctc.service.paymentservice;

import com.govt.irctc.enums.PaymentStatus;
import com.govt.irctc.enums.RefundStatus;
import com.govt.irctc.exceptions.BookingExceptions.BookingNotFoundException;
import com.govt.irctc.exceptions.PaymentExceptions.PaymentLinkGenerationException;
import com.govt.irctc.exceptions.PaymentExceptions.PaymentNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.model.Booking;
import com.govt.irctc.model.Payment;
import com.govt.irctc.repository.BookingRepository;
import com.govt.irctc.repository.PaymentRepository;
import com.govt.irctc.service.paymentservice.paymentgateways.PaymentGateway;
import com.govt.irctc.validation.TokenValidation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService{
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final TokenValidation tokenValidation;

    public PaymentServiceImpl(BookingRepository bookingRepository,
                              PaymentGateway paymentGateway, PaymentRepository paymentRepository,
                              TokenValidation tokenValidation) {
        this.bookingRepository = bookingRepository;
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.tokenValidation = tokenValidation;
    }

    @Override
    public String initiatePayment(Long pnr) throws BookingNotFoundException, PaymentLinkGenerationException, InvalidTokenException {
        Optional<Booking> booking = bookingRepository.findByPnr(pnr);

        if(booking.isEmpty()) {
            throw new BookingNotFoundException("Booking not found for this PNR");
        }

        Booking existingBooking = booking.get();

        if(tokenValidation.isTokenValid(existingBooking.getToken())) {
            throw new InvalidTokenException("Invalid token");
        }
        Optional<Payment> paymentExists = paymentRepository.findByBookingId(existingBooking.getId());

        if(paymentExists.isPresent()) {
            throw new PaymentLinkGenerationException("payment already done");
        }

        String paymentLink;
        try {
            paymentLink = paymentGateway.generatePaymentLink(existingBooking);
        } catch (Exception ex) {
            throw new PaymentLinkGenerationException("Failed to generate payment link for PNR: " + pnr);
        }

        Payment payment = new Payment();
        payment.setTransactionId(generateUniqueTransactionId());
        payment.setBooking(existingBooking);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setRefundStatus(RefundStatus.NA);

        paymentRepository.save(payment);
        return paymentLink;
    }

    public synchronized String generateUniqueTransactionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getPaymentStatus(Long transactionId) throws PaymentNotFoundException {
        Optional<Payment> payment = paymentRepository.findByPaymentId(transactionId);

        if(payment.isEmpty()) {
            throw new PaymentNotFoundException("payment not found");
        }
        return payment.get().getStatus().toString();
    }


    @Override
    public String getPaymentRefundStatus(Long transactionId) throws PaymentNotFoundException {
        Optional<Payment> payment = paymentRepository.findByPaymentId(transactionId);
        if(payment.isEmpty()) {
            throw new PaymentNotFoundException("payment not found");
        }
        return payment.get().getRefundStatus().toString();
    }
}
