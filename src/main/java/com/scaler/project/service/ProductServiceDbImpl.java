package com.scaler.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.scaler.project.dtos.CategoryDto;
import com.scaler.project.dtos.CreateProductDto;
import com.scaler.project.dtos.ProductDto;
import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;
import com.scaler.project.repositories.CategoriesRepository;
import com.scaler.project.repositories.ProductRepository;

@Service("SelfProductService")
@Primary
public class ProductServiceDbImpl implements ProductService{
	
	private ProductRepository pr;
	private CategoriesRepository cr;
	
	
	public ProductServiceDbImpl(ProductRepository pr, 
			CategoriesRepository cr) {
		this.pr = pr;
		this.cr = cr;
	}
	
	@Override
	public Product getProductById(Long productId) {
		Optional<Product> optional = pr.findById(productId);
        return optional.orElse(null);
	}

	@Override
	public Product createProduct(CreateProductDto createProductDto) {
		Product product = new Product();
		product.setTitle(createProductDto.getTitle());
		product.setPrice(createProductDto.getPrice());
		product.setDescription(createProductDto.getDescription());
		product.setImageUrl(createProductDto.getImage());
		
		Categories categoryFromDb = cr.findByTitle(createProductDto.getTitle()); 
		
		if(categoryFromDb == null) {
			Categories category = new Categories();
			category.setTitle(createProductDto.getTitle());
			categoryFromDb = cr.save(category);
		}
		product.setCategory(categoryFromDb);
		
		return pr.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return pr.findAll();
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		List<Categories> categories =  cr.findAll();
		List<CategoryDto> categoriesDtos = new ArrayList<CategoryDto>();
		for(Categories c : categories) {
			CategoryDto cd = new CategoryDto();
			cd.setCategory(c.getTitle());
			categoriesDtos.add(cd);
		}
		return categoriesDtos;
	}

	@Override
	public List<Product> getProductsByCategory(String category) {
		Optional<List<Product>> productList = pr.findAllBy(category);
        return productList.orElse(null);
	}

	@Override
	public void updateProduct(Long id, ProductDto productDto) {
        Product product = productDto.convertToProduct();

        Categories categoriesFromDB = cr.findByTitle(productDto.getTitle());

        if(categoriesFromDB == null) {
            Categories category = new Categories();
            category.setTitle(productDto.getTitle());
            categoriesFromDB = cr.save(category);
        }
        pr.updateBy(id, product.getTitle(), product.getPrice(),
				product.getDescription(), product.getImageUrl());
	}

	@Override
	public String deleteProduct(Long id) {
        pr.deleteBy(id);
		return  "Items with id: "+id+" deleted successfully";
	}

}
