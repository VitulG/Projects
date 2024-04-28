package com.scaler.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class BlackProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackProjectApplication.class, args);
	}
}