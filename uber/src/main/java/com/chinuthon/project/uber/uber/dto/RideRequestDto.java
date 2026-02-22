package com.chinuthon.project.uber.uber.dto;

import com.chinuthon.project.uber.uber.entities.Rider;
import com.chinuthon.project.uber.uber.entities.enums.PaymentMethod;
import com.chinuthon.project.uber.uber.entities.enums.RideRequestStatus;
import org.geolatte.geom.Point;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RideRequestDto {
    private Long id;
    private PointDto pickUpLocation;
    private PointDto dropOffLocation;
    private LocalDateTime requestTime;
    private RiderDto rider;
    private PaymentMethod paymentMethod;
    private RideRequestStatus rideRequestStatus;
    private Double fare;
}
