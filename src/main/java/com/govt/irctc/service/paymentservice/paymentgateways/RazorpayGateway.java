package com.govt.irctc.service.paymentservice.paymentgateways;


import com.govt.irctc.exceptions.PaymentExceptions.PaymentLinkGenerationException;
import com.govt.irctc.model.Booking;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RazorpayGateway implements PaymentGateway {

    @Override
    public String generatePaymentLink(Booking booking) throws PaymentLinkGenerationException {
        try {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_MZEtxncR4Vkqcn", 
                    "UYNI98uIc9BIlHddTmzrCl9N");

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", booking.getTotalPrice()*100);
            paymentLinkRequest.put("currency","INR");
            paymentLinkRequest.put("accept_partial",false);
            Long validTill = System.currentTimeMillis()/1000+3600;
            paymentLinkRequest.put("expire_by", validTill);
            paymentLinkRequest.put("reference_id", "BT001");
            paymentLinkRequest.put("description","Payment for policy no #1");

            JSONObject customer = new JSONObject();
            customer.put("name", booking.getUserBookings().getUserName());
            customer.put("contact", booking.getUserBookings().getUserPhoneNumber().toString());
            customer.put("email", booking.getUserBookings().getUserEmail());
            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("sms",true);
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);
            paymentLinkRequest.put("reminder_enable",true);

            JSONObject notes = new JSONObject();
            notes.put("policy_name","Booking Ticket");
            paymentLinkRequest.put("notes",notes);

            paymentLinkRequest.put("callback_url","https://google.com/");
            paymentLinkRequest.put("callback_method","get");

            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            return payment.get("short_url").toString();
        }catch(Exception ex) {
            throw new PaymentLinkGenerationException("Unable to create a payment link");
        }
    }
}
