package com.scaler.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scaler.project.model.Categories;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long>{
	Categories findByTitle(String title);
	Categories save(Categories category);
	List<Categories> findAll();
	Optional<Categories> findById(Long id);
}
