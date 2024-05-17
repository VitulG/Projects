package com.scaler.project.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class ApplicationConfigurations {
	@Bean
	public RestTemplate getRestTemplateObject() {
		return new RestTemplate();
	}
}
