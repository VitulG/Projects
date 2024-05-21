package com.scaler.project.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.scaler.project.dto.CategoryDto;
import com.scaler.project.dto.CreateProductDto;
import com.scaler.project.dto.ProductDto;
import com.scaler.project.model.Product;

@Service("fakeStoreService")
public class ProductServiceImpl implements ProductService{
	
	private RestTemplate rt;
	private RedisTemplate<String, Object> redisTemplate;
	
	public ProductServiceImpl(RestTemplate rt,
							  RedisTemplate<String, Object> redisTemplate) {
		this.rt = rt;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Product getProductById(Long id) {

		Product existingProduct = (Product) redisTemplate.opsForValue().get(String.valueOf(id));
		if(existingProduct != null) {
			return existingProduct;
		}

		ResponseEntity<ProductDto> response = rt.getForEntity("https://fakestoreapi.com/products/"+id, 
				ProductDto.class);
		
		ProductDto receiveDto = response.getBody();

		Product finalProduct = receiveDto.convertToProduct();

		redisTemplate.opsForValue().set(String.valueOf(id), finalProduct);

		return finalProduct;
	}

	@Override
	public Long createProduct(CreateProductDto createProductDto) {
		// TODO Auto-generated method stub
		
		ResponseEntity<CreateProductDto> response = rt
				.postForEntity("https://fakestoreapi.com/products", 
						createProductDto, CreateProductDto.class);
		
		CreateProductDto receiveDto = response.getBody();
		receiveDto.convertToProduct();

		return receiveDto.getId();
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

	@Override
	public List<Product> getProducts(int limit) {
		ResponseEntity<ProductDto[]> response = rt.getForEntity(
				"https://fakestoreapi.com/products?limit="+limit,
				ProductDto[].class
		);

		if(response.getBody() == null) {
			return List.of();
		}

		List<Product> products = new ArrayList<>();
		for(ProductDto productDto : response.getBody()) {
			products.add(productDto.convertToProduct());
		}
		return products;
	}

	@Override
	public List<Product> sortProducts(String sortType) {
		ResponseEntity<ProductDto[]> response = rt.getForEntity(
				"https://fakestoreapi.com/products",
				ProductDto[].class
		);

		if(response.getBody() == null) {
			return List.of();
		}

		List<Product> products = new ArrayList<>();
		for(ProductDto productDto : response.getBody()) {
			products.add(productDto.convertToProduct());
		}
		if(sortType.equalsIgnoreCase("desc")) {
			products.sort(Comparator.comparing(Product::getProductId).reversed());
		}
		return products;
	}

	@Override
	public List<Product> getProductsWithLimitSort(int limit, String sortType) {
		ResponseEntity<ProductDto[]> response = rt.getForEntity(
				"https://fakestoreapi.com/products?limit="+limit,
				ProductDto[].class
		);

		if(response.getBody() == null) {
			return null;
		}

		List<Product> products = Arrays.stream(response.getBody())
				.map(ProductDto::convertToProduct)
				.collect(Collectors.toList());

		for(ProductDto productDto : response.getBody()) {
			products.add(productDto.convertToProduct());
		}

		if(sortType.equalsIgnoreCase("desc")) {
			products.sort(Comparator.comparing(Product::getProductId).reversed());
		}
		return products;
	}

}
