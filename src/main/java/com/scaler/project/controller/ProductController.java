package com.scaler.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scaler.project.dto.CategoryDto;
import com.scaler.project.dto.CreateProductDto;
import com.scaler.project.dto.ProductDto;
import com.scaler.project.model.Product;
import com.scaler.project.service.ProductService;

@RestController
public class ProductController {
	
	private ProductService ps;

	public ProductController(@Qualifier("fakeStoreService") ProductService ps) {
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

	// to limit the result
	@RequestMapping(value = "/products/limit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<List<Product>> limitProducts(@RequestParam(defaultValue="3")
														   int limit) {
		List<Product> productListWithLimit = ps.getProducts(limit);
		return new ResponseEntity<>(productListWithLimit, HttpStatus.OK);
	}

	// to sort the products
	@GetMapping("/products/sort")
	@ResponseBody
	public ResponseEntity<List<Product>> sortProducts(@RequestParam(defaultValue = "asc")
													  String sortType) {
		List<Product> sortedProducts = ps.sortProducts(sortType);
		return new ResponseEntity<>(sortedProducts, HttpStatus.OK);
	}

	// get products with limit and sort based
	@GetMapping("/products/limit-sort")
	@ResponseBody
	public ResponseEntity<List<Product>> productsWithLimitAndSort(@RequestParam(defaultValue = "3") int limit,
																  @RequestParam(defaultValue = "asc") String sortType) {
		List<Product> products = ps.getProductsWithLimitSort(limit, sortType);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
}
