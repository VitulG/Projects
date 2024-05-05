package com.scaler.project;

import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;
import com.scaler.project.repositories.CategoriesRepository;
import com.scaler.project.repositories.ProductRepository;
import com.scaler.project.repositories.projections.ProductProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class BlackProjectApplicationTests {

	@Autowired
	ProductRepository pr;

	@Autowired
	CategoriesRepository cr;

	@Test
	void contextLoads() {
	}

}
