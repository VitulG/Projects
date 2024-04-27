package com.scaler.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.scaler.project.dtos.CategoryDto;
import com.scaler.project.dtos.CreateProductDto;
import com.scaler.project.dtos.ProductDto;
import com.scaler.project.models.Categories;
import com.scaler.project.models.Product;

@Service("fakeStoreService")
public class ProductServiceImpl implements ProductService{
	
	private RestTemplate rt;
	
	public ProductServiceImpl(RestTemplate rt) {
		this.rt = rt;
	}

	@Override
	public Product getProductById(Long id) {
		
		ResponseEntity<ProductDto> response = rt.getForEntity("https://fakestoreapi.com/products/"+id, 
				ProductDto.class);
		
		ProductDto receiveDto = response.getBody();
		return receiveDto.convertToProduct();
	}

	@Override
	public Product createProduct(CreateProductDto createProductDto) {
		// TODO Auto-generated method stub
		
		ResponseEntity<CreateProductDto> response = rt
				.postForEntity("https://fakestoreapi.com/products", 
						createProductDto, CreateProductDto.class);
		
		CreateProductDto receiveDto = response.getBody();
		return receiveDto.convertToProduct();
	}

	@Override
	public List<Product> getAllProducts() {
		// TODO Auto-generated method stub
		ResponseEntity<ProductDto[]> responseList = rt
				.getForEntity("https://fakestoreapi.com/products", 
						ProductDto[].class);
		
		List<Product> productsList = new ArrayList<Product>();
		
		for(ProductDto product : responseList.getBody()) {
			productsList.add(product.convertToProduct());
		}
		return productsList;
	}

	@Override
	public List<CategoryDto> getAllCategories() {
		ResponseEntity<String[]> responseList = rt.
				getForEntity("https://fakestoreapi.com/products/categories",
						String[].class);
		List<CategoryDto> allCategories = new ArrayList<CategoryDto>();
		
		for(String category : responseList.getBody()) {
			CategoryDto cd = new CategoryDto();
			cd.setCategory(category);
			allCategories.add(cd);	
		}
		return allCategories;
	}

	@Override
	public List<Product> getProductsByCategory(String category) {
		ResponseEntity<ProductDto[]> response = rt.
				getForEntity("https://fakestoreapi.com/products/category/"+category, 
						ProductDto[].class);
		List<Product> list = new ArrayList<Product>();
		
		for(ProductDto product : response.getBody()) {
			list.add(product.convertToProduct());
		}
		return list;
	}

	@Override
	public void updateProduct(Long id, ProductDto product) {
		
		HttpEntity<ProductDto> entity = new HttpEntity<ProductDto>(product, null);
		
		ResponseEntity<ProductDto> response = rt.exchange(
                "https://fakestoreapi.com/products/{id}",
                HttpMethod.PUT,
                entity,
                ProductDto.class,
                id);
		response.getBody().convertToProduct();
	}

	@Override
	public String deleteProduct(Long id) {
		
		ProductDto pd = new ProductDto();
		HttpEntity<ProductDto> he = new HttpEntity<>(pd, null);
		ResponseEntity<ProductDto> response = rt.exchange(
				"https://fakestoreapi.com/products/{id}", 
				HttpMethod.DELETE, 
				he, 
				ProductDto.class, 
				id);
		response.getBody().convertToProduct();
		return "Items with id: "+id+" deleted successfully";
	}

}
