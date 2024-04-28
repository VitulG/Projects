package com.scaler.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product extends BaseModel implements Comparable<Product>{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long productId;
	private String title;
	private Double price;
	
	@ManyToOne(cascade = {CascadeType.PERSIST})
	private Categories category;
	
	private String description;
	private String imageUrl;

	@Override
	public int compareTo(Product product) {
		return this.getProductId().compareTo(product.getProductId());
	}
}
