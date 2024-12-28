package com.govt.irctc.repository;

import com.govt.irctc.enums.CompartmentType;
import com.govt.irctc.model.Compartment;
import com.govt.irctc.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompartmentRepository extends JpaRepository<Compartment, Long> {
    Optional<Compartment> findByCompartmentNumber(String compartmentNumber);
    List<Compartment> findAllByTrainAndCompartmentType(Train train, CompartmentType type);
}
