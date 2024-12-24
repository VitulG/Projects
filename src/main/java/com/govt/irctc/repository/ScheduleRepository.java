package com.govt.irctc.repository;

import com.govt.irctc.model.Schedule;
import com.govt.irctc.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByScheduleTitle(String scheduleTitle);
    List<Schedule> findAllByTrain(Train train);
}
