package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver,Long> {

    // ST_Distance(point1, point2) calculates the distance between two points
    // ST_DWithin(point1, point2, distance) checks if the distance between two points is within a specified distance

    @Query("SELECT d.* , ST_Distance(d.currentLocation, :pickUpLocation) AS distance " +
            "FROM Driver AS d " +
            "WHERE available = true AND ST_DWithin(d.currentLocation, :pickUpLocation, 10000) " + // 5000 meters = 5 km
            "ORDER BY distance " +
            "LIMIT 10")
    List<Driver> findTenNearestDrivers(Point pickUpLocation);
}
