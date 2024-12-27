package com.govt.irctc.repository;

import com.govt.irctc.model.Platform;
import com.govt.irctc.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    public boolean existsByPlatformNumberAndStation(long platformNumber, Station station);
}
