package com.scaler.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhiteMoneyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhiteMoneyApplication.class, args);
	}

}
