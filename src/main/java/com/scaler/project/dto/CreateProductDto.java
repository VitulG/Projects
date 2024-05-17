package com.scaler.project.dto;

import com.scaler.project.model.Categories;
import com.scaler.project.model.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDto {
	private Long id;
	private String productTitle;
	private String categoryTitle;
	private Double price;
	private String category;
	private String description;
	private String image;

	public Product convertToProduct() {
		
		Product product = new Product();
		product.setProductId(this.id);
		product.setTitle(getProductTitle());
		product.setPrice(getPrice());
		product.setDescription(getDescription());
		product.setImageUrl(getImage());
		
		Categories cat = new Categories();
		cat.setTitle(getCategoryTitle());
		product.setCategory(cat);
		
		return product;
	}
}
