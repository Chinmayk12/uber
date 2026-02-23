package com.chinuthon.project.uber.uber.stategies;

import com.chinuthon.project.uber.uber.entities.RideRequest;

public interface RideFareCalculationStrategy {
    double RIDE_FARE_MULTIPLIER = 10;
    double calculateFare(RideRequest rideRequest);
}
