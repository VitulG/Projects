package com.scaler.project.repositories;

import java.util.List;
import java.util.Optional;

import com.scaler.project.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scaler.project.models.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>{
	Categories findByTitle(String title);
	Categories save(Categories category);
	List<Categories> findAll();
}
