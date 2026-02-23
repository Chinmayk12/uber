package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.entities.RideRequest;
import com.chinuthon.project.uber.uber.exceptions.ResourceNotFoundException;
import com.chinuthon.project.uber.uber.repositories.RideRequestRepository;
import com.chinuthon.project.uber.uber.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(()-> new ResourceNotFoundException("RideRequest not found with id: " + rideRequestId));
    }

    @Override
    public void update(RideRequest rideRequest) {
        rideRequestRepository.findById(rideRequest.getId())
                .orElseThrow(()-> new ResourceNotFoundException("RideRequest not found with id: " + rideRequest.getId()));

        // if the ride request exists, save the updated ride request
        rideRequestRepository.save(rideRequest);
    }
}
