package com.govt.irctc.repository;

import com.govt.irctc.model.Compartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
}
