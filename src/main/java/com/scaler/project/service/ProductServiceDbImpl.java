package com.scaler.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.scaler.project.dto.CategoryDto;
import com.scaler.project.dto.CreateProductDto;
import com.scaler.project.dto.ProductDto;
import com.scaler.project.model.Categories;
import com.scaler.project.model.Product;
import com.scaler.project.repository.CategoriesRepository;
import com.scaler.project.repository.ProductRepository;

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
	public Long createProduct(CreateProductDto createProductDto) {
		Product product = new Product();
		product.setTitle(createProductDto.getProductTitle());
		product.setPrice(createProductDto.getPrice());
		product.setDescription(createProductDto.getDescription());
		product.setImageUrl(createProductDto.getImage());
		
		Categories categoryFromDb = cr.findByTitle(createProductDto.getCategoryTitle());
		
		if(categoryFromDb == null) {
			Categories category = new Categories();
			category.setTitle(createProductDto.getCategoryTitle());
			categoryFromDb = cr.save(category);
		}
		product.setCategory(categoryFromDb);
		pr.save(product);

        return product.getProductId();
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

        Categories categoriesFromDB = cr.findByTitle(productDto.getCategoryTitle());

        if(categoriesFromDB == null) {
            Categories category = new Categories();
            category.setTitle(productDto.getCategoryTitle());
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

	@Override
	public List<Product> getProducts(int limit) {
		return List.of();
	}

	@Override
	public List<Product> sortProducts(String sortType) {
		return List.of();
	}

	@Override
	public List<Product> getProductsWithLimitSort(int limit, String sortType) {
		return List.of();
	}

}
