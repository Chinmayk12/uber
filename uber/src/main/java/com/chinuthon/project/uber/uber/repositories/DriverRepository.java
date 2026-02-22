package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Driver;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    // ST_Distance(point1, point2) calculates the distance between two points
    // ST_DWithin(point1, point2, distance) checks if the distance between two
    // points is within a specified distance
    // Always use Kakab case for table and column names in native queries to avoid issues with case sensitivity
    @Query(value = """
            SELECT *
            FROM driver d
            WHERE d.available = true
            AND ST_DWithin(d.current_location, :pickUpLocation, 10000)
            ORDER BY ST_Distance(d.current_location, :pickUpLocation)
            LIMIT 10
            """, nativeQuery = true)
    List<Driver> findTenNearestDrivers(Point pickUpLocation);

    @Query(value = """
            SELECT *
            FROM driver d
            WHERE d.available = true
            AND ST_DWithin(d.current_location, :pickUpLocation, 15000)
            ORDER BY d.rating DESC,
            LIMIT 10
            """, nativeQuery = true)
    List<Driver> findTenNearbyTopRatedDrivers(Point pickUpLocation);

}

