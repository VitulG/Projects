package com.scaler.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.scaler.project.dto.CategoryDto;
import com.scaler.project.dto.CreateProductDto;
import com.scaler.project.dto.ProductDto;
import com.scaler.project.model.Product;
import com.scaler.project.service.ProductService;

@RestController
public class ProductController {
	
	private ProductService ps;

	public ProductController(@Qualifier("SelfProductService") ProductService ps) {
		this.ps = ps;
	}

	@GetMapping("/productService/health")
	public ResponseEntity<String> getHealth() {
		String msg = "health is Ok";
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Product> getProductById(@PathVariable("id") Long id){
		Product result = ps.getProductById(id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> createProduct(@RequestBody CreateProductDto cpd) {
		Long productId = ps.createProduct(cpd);
		String msg = "Product has been created with product id: "+productId;
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	@GetMapping("/products")
	@ResponseBody
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = ps.getAllProducts();
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
	
	@GetMapping("/products/categories")
	@ResponseBody
	public ResponseEntity<List<CategoryDto>> getAllCategories() {
		List<CategoryDto> categories = ps.getAllCategories();
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
	
	@GetMapping("/products/categories/{category}")
	@ResponseBody
	public ResponseEntity<List<Product>> 
		getProductsByCategory(@PathVariable("category") String category) {
		
		List<Product> productsByCategory = ps.getProductsByCategory(category);
		return new ResponseEntity<>(productsByCategory, HttpStatus.OK);
	}
	
	@RequestMapping(value ="/products/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<String> updateProduct(@PathVariable("id") Long id, @RequestBody ProductDto productDto) {
		ps.updateProduct(id, productDto);
		String msg = "Updated with id: "+id+" successfully updated";
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}
	
	@RequestMapping(value="/products/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
		String msg = ps.deleteProduct(id);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}
}
