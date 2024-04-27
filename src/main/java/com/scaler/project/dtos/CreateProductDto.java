package com.scaler.project.dtos;

import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDto {
	private Long id;
	private String title;
	private Double price;
	private String category;
	private String description;
	private String image;

	public Product convertToProduct() {
		
		Product product = new Product();
		product.setProductId(this.id);
		product.setTitle(getTitle());
		product.setPrice(getPrice());
		product.setDescription(getDescription());
		product.setImageUrl(getImage());
		
		Categories cat = new Categories();
		cat.setTitle(getTitle());
		product.setCategory(cat);
		
		return product;
	}
}
