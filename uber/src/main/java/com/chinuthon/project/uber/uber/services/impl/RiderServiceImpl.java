package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.RideDto;
import com.chinuthon.project.uber.uber.dto.RideRequestDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.RideRequest;
import com.chinuthon.project.uber.uber.entities.Rider;
import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.entities.enums.RideRequestStatus;
import com.chinuthon.project.uber.uber.exceptions.ResourceNotFoundException;
import com.chinuthon.project.uber.uber.repositories.RideRequestRepository;
import com.chinuthon.project.uber.uber.repositories.RiderRepository;
import com.chinuthon.project.uber.uber.services.RiderService;
import com.chinuthon.project.uber.uber.stategies.RideStrategyManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideStrategyManager rideStrategyManager;  // To use the strategy manager for fare calculation and driver matching

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {
        Rider rider = getCurrentRider(); // Get the current rider (this can be done through authentication context in a real application)

        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);
        rideRequest.setRider(rider);
        // Calculating Fare
        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        // Save the ride request to the database
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        // find drivers
        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDrivers(rideRequest);
        // TODO : Notify the drivers about the new ride request and handle their responses (accept/reject)

        //log.info(rideRequest.toString());
        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {
        return null;
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {
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
    public Rider createNewRider(User user) {
        Rider rider = Rider.builder()
                .user(user)
                .rating(0.0)
                .build();
        return riderRepository.save(rider);
    }

    @Override
    public Rider getCurrentRider() {
        return riderRepository.findById(1L).orElseThrow(()-> new ResourceNotFoundException("Rider not found with id "+1L));
    }
}
