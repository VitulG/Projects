package com.govt.irctc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.govt.irctc.repository")
@EnableElasticsearchRepositories(basePackages = "com.govt.irctc.elasticsearchrepository")
public class RailApplication {

	public static void main(String[] args) {
		SpringApplication.run(RailApplication.class, args);
	}

}