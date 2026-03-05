package com.chinuthon.project.uber.uber.repositories;

import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Rating;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface RatingRepository extends JpaRepository<Rating,Long> {
    List<Rating>findByRider(Rider rider);
    List<Rating>findByDriver(Driver driver);

    Optional<Rating> findByRide(Ride ride);
}
