package com.scaler.project.service;

import com.scaler.project.dto.SearchDtos;
import com.scaler.project.model.Product;
import com.scaler.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private ProductRepository productRepository;

    @Autowired
    public SearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getSearchPage(SearchDtos searchDtos) {
        Sort sort =  Sort.by("price").descending();
        Pageable page = PageRequest.of(searchDtos.getPageNumber(), searchDtos.getPageSize(), sort);
        return productRepository.findByTitleContaining(searchDtos.getQuery(), page);
    }
}
