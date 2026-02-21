package com.chinuthon.project.uber.uber.services;

import com.chinuthon.project.uber.uber.dto.RideRequestDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

// This is a service that will be called by another services not by the controllers.
// It will have the business logic of the application. It will be called by the controllers
public interface RideService {

    Ride getRideById(Long rideId);

    void matchWithDriver(RideRequestDto rideRequestDto);

    Ride createNewRide(RideRequestDto rideRequestDto, Driver driver);

    Ride updateRideStatus(Long rideId, String status);

    Page<Ride> getAllRidesOfRider(Long riderId, PageRequest pageRequest);

    Page<Ride> getAllRidesOfDriver(Long driverId, PageRequest pageRequest);

}
