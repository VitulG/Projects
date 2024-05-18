package com.scaler.weather.repository;

import com.scaler.weather.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location save(Location location);
}
