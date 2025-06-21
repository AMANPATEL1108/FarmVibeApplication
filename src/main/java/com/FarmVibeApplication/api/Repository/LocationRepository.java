package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByCity(String city);
    @Query("SELECT DISTINCT l.city FROM Location l")
    List<String> findDistinctCities();
}
