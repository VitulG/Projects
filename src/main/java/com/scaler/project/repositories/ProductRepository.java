package com.scaler.project.repositories;

import com.scaler.project.models.Categories;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scaler.project.models.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	Product save(Product product);

	@Query("SELECT p FROM Product p where p.productId = :productId")
    Optional<Product> findById(Long productId);

    @Query("SELECT p FROM Product p where p.title = :title")
    Optional<List<Product>> findAllBy(String title);

    @Transactional
    @Modifying
    @Query("DELETE FROM Product p WHERE p.productId = :productId")
    void deleteBy(@Param("productId") Long productId);

    @Transactional
    @Modifying
    @Query("UPDATE Product p SET p.title= :title, p.price = :price, p.description=:description, p.imageUrl=:image WHERE p.productId = :productId")
    void updateBy(@Param("productId")  Long productId, @Param("title") String title, @Param("price") Double price,
                  @Param("description") String description, @Param("image") String image);

}