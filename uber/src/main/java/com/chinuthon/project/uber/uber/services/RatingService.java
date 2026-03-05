package com.chinuthon.project.uber.uber.services;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.RiderDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.Rider;

public interface RatingService {
    DriverDto rateDriver(Ride ride, Integer rating);
    RiderDto rateRider(Ride ride, Integer rating);
    void createNewRating(Ride ride);
}
