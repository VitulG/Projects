package com.scaler.project;

import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;
import com.scaler.project.repositories.CategoriesRepository;
import com.scaler.project.repositories.ProductRepository;
import com.scaler.project.repositories.projections.ProductProjection;
import jakarta.transaction.Transactional;
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

	@Test
	@Transactional
	void testingQuery1() {
		Categories category = cr.findByTitle("Books");
		System.out.println(category.getProducts());
	}

	@Test
	void testingQuery2() {
		Optional<Categories> categoryById = cr.findById(2L);
		System.out.println(categoryById.get().getTitle());
	}

	@Test
	@Transactional
	void testingQuery3() {
		List<Categories> categories = cr.findAll();
		for(Categories c : categories) {
			System.out.println(c.getCategoryId()+" "+c.getTitle());
			for(Product p : c.getProducts()) {
				System.out.println(p.getProductId() +" "+p.getTitle());
			}
		}
	}
}
