package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.dto.RideRequestDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.RideRequest;
import com.chinuthon.project.uber.uber.entities.enums.RideRequestStatus;
import com.chinuthon.project.uber.uber.entities.enums.RideStatus;
import com.chinuthon.project.uber.uber.exceptions.ResourceNotFoundException;
import com.chinuthon.project.uber.uber.repositories.RideRepository;
import com.chinuthon.project.uber.uber.services.RideRequestService;
import com.chinuthon.project.uber.uber.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    protected final RideRequestService rideRequestService;
    private final RideRepository rideRepository;
    private final ModelMapper modelMapper;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(()-> new ResourceNotFoundException("Ride not found with this id : "+rideId));
    }

    @Override
    public void matchWithDriver(RideRequestDto rideRequestDto) {

    }

    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRM);

        Ride ride = modelMapper.map(rideRequest, Ride.class);
        ride.setRideStatus(RideStatus.CONFIRMED);
        ride.setDriver(driver);
        ride.setOtp(generateRandomOtp()); // Generate a random OTP for the ride
        ride.setId(null);

        rideRequestService.update(rideRequest);
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus status) {
        ride.setRideStatus(status);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Long riderId, PageRequest pageRequest) {
        return null;
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Long driverId, PageRequest pageRequest) {
        return null;
    }

    private String generateRandomOtp(){
        Random random = new Random();
        int otpInt = random.nextInt(10000); // Generate a random number between 0 and 9999
        return String.format("%04d", otpInt); // Format it as a 4-digit
    }
}
