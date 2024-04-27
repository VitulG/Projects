package com.scaler.project.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.scaler.project.controller.ProductController;
import com.scaler.project.service.ProductServiceDbImpl;


@Configuration
public class ApplicationConfigurations {
	@Bean
	public RestTemplate getRestTemplateObject() {
		return new RestTemplate();
	}
}
