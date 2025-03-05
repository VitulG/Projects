package com.govt.irctc.repository;

import com.govt.irctc.model.Train;
import jakarta.transaction.Transactional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Optional<Train> findByTrainNumber(Long trainNumber);

    @Modifying
    @Transactional
    void deleteByTrainNumber(Long trainNumber);
}
