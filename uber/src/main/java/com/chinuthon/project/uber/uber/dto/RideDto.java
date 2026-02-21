package com.chinuthon.project.uber.uber.dto;

import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.Rider;
import com.chinuthon.project.uber.uber.entities.enums.PaymentMethod;
import com.chinuthon.project.uber.uber.entities.enums.RideStatus;
import jakarta.persistence.*;
import org.geolatte.geom.Point;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class RideDto {
    private Long id;
    private Point pickupLocation;
    private Point dropOffLocation;

    private LocalDateTime createdTime;
    private RiderDto rider;
    private DriverDto driver;

    private PaymentMethod paymentMethod;
    private RideStatus rideStatus;
    private Double fare;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
