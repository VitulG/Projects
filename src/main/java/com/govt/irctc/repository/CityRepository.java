package com.govt.irctc.repository;

import com.govt.irctc.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    public Optional<City> findByCityName(String cityName);
}
