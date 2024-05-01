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

	@Test
	void chekQuery() {
		ProductProjection product = pr.getProductIdAndTitleById(1L);
		System.out.println(product.getId());
		System.out.println(product.getTitle()); 
	}

	@Test
	void checkQuery1() {
		Optional<List<Product>> listOFProducts = pr.findAllBy("Nokia");
		for(Product product : listOFProducts.get()) {
			System.out.println(product.getProductId() +" "+product.getTitle());
		}
	}

	@Test
	void checkQuery2() {
		List<Categories> categories = cr.findAll();
		for(Categories category : categories) {
			for(Product product  : category.getProducts()) {
				System.out.println(product.getTitle()+" "+product.getPrice());
			}
		}
	}
}
