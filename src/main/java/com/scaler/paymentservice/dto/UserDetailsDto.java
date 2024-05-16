package com.scaler.paymentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private String userName;
    private Long orderId;
    private Double amount;
    private Long phoneNumber;
    private String email;
    private Long quantity;
}
