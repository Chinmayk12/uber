package com.chinuthon.project.uber.uber.services;

import com.chinuthon.project.uber.uber.entities.RideRequest;

public interface RideRequestService {
    RideRequest findRideRequestById(Long rideRequestId);

    void update(RideRequest rideRequest);
}
