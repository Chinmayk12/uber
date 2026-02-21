package com.chinuthon.project.uber.uber.stategies;

import com.chinuthon.project.uber.uber.dto.RideRequestDto;
import com.chinuthon.project.uber.uber.entities.Driver;

import java.util.List;

public interface DriverMatchingStrategy {
    List<Driver> findMatchingDrivers(RideRequestDto rideRequestDto);

}
