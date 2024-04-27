package com.scaler.project.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Categories extends BaseModel{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int categoryId;
	private String title;
	
	@OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE})
	@JsonIgnore
	private List<Product> products;

}
