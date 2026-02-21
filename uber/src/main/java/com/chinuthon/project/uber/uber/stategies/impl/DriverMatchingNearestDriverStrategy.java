package com.chinuthon.project.uber.uber.stategies.impl;

import com.chinuthon.project.uber.uber.dto.RideRequestDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.stategies.DriverMatchingStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverMatchingNearestDriverStrategy implements DriverMatchingStrategy {
    @Override
    public List<Driver> findMatchingDrivers(RideRequestDto rideRequestDto) {
        return List.of();
    }
}
