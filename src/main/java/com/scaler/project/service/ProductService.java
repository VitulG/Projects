package com.scaler.project.service;

import java.util.List;

import com.scaler.project.dtos.CategoryDto;
import com.scaler.project.dtos.CreateProductDto;
import com.scaler.project.dtos.ProductDto;
import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;

public interface ProductService {
	public Product getProductById(Long id);
	public Long createProduct(CreateProductDto createProductDto);
	public List<Product> getAllProducts();
	public List<CategoryDto> getAllCategories();
	public List<Product> getProductsByCategory(String category);
	public void updateProduct(Long id, ProductDto product);
	public String deleteProduct(Long id);
}
