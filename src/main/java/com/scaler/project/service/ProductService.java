package com.scaler.project.service;

import java.util.List;

import com.scaler.project.dto.CategoryDto;
import com.scaler.project.dto.CreateProductDto;
import com.scaler.project.dto.ProductDto;
import com.scaler.project.model.Product;

public interface ProductService {
	public Product getProductById(Long id);
	public Long createProduct(CreateProductDto createProductDto);
	public List<Product> getAllProducts();
	public List<CategoryDto> getAllCategories();
	public List<Product> getProductsByCategory(String category);
	public void updateProduct(Long id, ProductDto product);
	public String deleteProduct(Long id);
	public List<Product> getProducts(int limit);
	public List<Product> sortProducts(String sortType);
	public List<Product> getProductsWithLimitSort(int limit, String sortType);
}
