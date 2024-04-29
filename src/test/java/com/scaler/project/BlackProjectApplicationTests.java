package com.scaler.project;

import com.scaler.project.models.Product;
import com.scaler.project.repositories.ProductRepository;
import com.scaler.project.repositories.projections.ProductProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class BlackProjectApplicationTests {

	@Autowired
	ProductRepository pr;

	@Test
	void contextLoads() {
	}

	@Test
	void chekQuery() {
		ProductProjection product = pr.getProductIdAndTitleById(1L);
		System.out.println(product.getId());
		System.out.println(product.getTitle());
	}
}
