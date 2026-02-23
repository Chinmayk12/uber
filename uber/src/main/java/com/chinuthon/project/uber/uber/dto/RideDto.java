package com.chinuthon.project.uber.uber.dto;

import com.chinuthon.project.uber.uber.entities.enums.PaymentMethod;
import com.chinuthon.project.uber.uber.entities.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideDto {
    private Long id;
    private PointDto pickupLocation;
    private PointDto dropOffLocation;

    private LocalDateTime createdTime;
    private RiderDto rider;
    private DriverDto driver;

    private PaymentMethod paymentMethod;
    private RideStatus rideStatus;
    private Double fare;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
