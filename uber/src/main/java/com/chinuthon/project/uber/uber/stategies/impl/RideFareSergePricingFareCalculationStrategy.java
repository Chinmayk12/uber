package com.chinuthon.project.uber.uber.stategies.impl;

import com.chinuthon.project.uber.uber.entities.RideRequest;
import com.chinuthon.project.uber.uber.services.DistanceService;
import com.chinuthon.project.uber.uber.stategies.RideFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideFareSergePricingFareCalculationStrategy implements RideFareCalculationStrategy {
    private final DistanceService distanceService;
    private static final double SURGE_FACTOR = 2; // Surge pricing multiplier

    @Override
    public double calculateFare(RideRequest rideRequest) {
        double distance = distanceService.calculateDistance(rideRequest.getPickUpLocation(),
                rideRequest.getDropOffLocation());

        return distance * RIDE_FARE_MULTIPLIER*SURGE_FACTOR;
    }
}
