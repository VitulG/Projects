package com.scaler.project.controller;

import com.scaler.project.dto.SearchDtos;
import com.scaler.project.model.Product;
import com.scaler.project.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {
    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @RequestMapping(value = "/products/search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Page<Product>> searchProduct(@RequestBody SearchDtos searchDtos) {
        Page<Product> productsOnPage = searchService.getSearchPage(searchDtos);
        return new ResponseEntity<>(productsOnPage, HttpStatus.OK);
    }
}
