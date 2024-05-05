package com.scaler.project.dtos;

import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
	private Long id;
	private String productTitle;
	private Double price;
	private String category;
	private String description;
	private String imageUrl;
	private String categoryTitle;
	
	public Product convertToProduct() {
		Product product = new Product();
		product.setProductId(getId());
		product.setTitle(getProductTitle());
		product.setPrice(getPrice());
		product.setDescription(getDescription());
		product.setImageUrl(getImageUrl());
		
		Categories cat = new Categories();
		cat.setTitle(getCategoryTitle());
		product.setCategory(cat);
		return product;
	}
}
