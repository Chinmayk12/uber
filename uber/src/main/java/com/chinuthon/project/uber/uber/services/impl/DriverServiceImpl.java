package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.RideDto;
import com.chinuthon.project.uber.uber.dto.RiderDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.RideRequest;
import com.chinuthon.project.uber.uber.entities.enums.RideRequestStatus;
import com.chinuthon.project.uber.uber.repositories.DriverRepository;
import com.chinuthon.project.uber.uber.services.DriverService;
import com.chinuthon.project.uber.uber.services.RideRequestService;
import com.chinuthon.project.uber.uber.services.RideService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING)) {
            throw new RuntimeException("RideRequest cannot be accepted. Current status: " + rideRequest.getRideRequestStatus());
        }

        // Checking is the driver is available
        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.getAvailable()) {
            throw new RuntimeException("Driver is not available to accept the ride request.");
        }

        Ride ride = rideService.createNewRide(rideRequest, currentDriver);

        return modelMapper.map(ride, RideDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    public RideDto startRide(Long rideId) {
        return null;
    }

    @Override
    public RideDto endRide(Long rideId) {
        return null;
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {
        return null;
    }

    @Override
    public DriverDto getMyProfile() {
        return null;
    }

    @Override
    public List<RideDto> getAllMyRides() {
        return List.of();
    }

    @Override
    public Driver getCurrentDriver() {
        // Returning dummy driver for now, in a real application this would be retrieved from the authentication context
        return driverRepository.findById(1L).orElseThrow(() ->
                new RuntimeException("Driver not found with id"+1L));
    }
}
