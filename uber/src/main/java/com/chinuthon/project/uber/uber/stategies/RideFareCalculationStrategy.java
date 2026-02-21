package com.chinuthon.project.uber.uber.stategies;

import com.chinuthon.project.uber.uber.dto.RideRequestDto;

public interface RideFareCalculationStrategy {
    double calculateFare(RideRequestDto rideRequestDto);
}
