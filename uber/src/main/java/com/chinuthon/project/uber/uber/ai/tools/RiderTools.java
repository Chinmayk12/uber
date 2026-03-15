package com.chinuthon.project.uber.uber.ai.tools;

import com.chinuthon.project.uber.uber.ai.service.RideVectorService;
import com.chinuthon.project.uber.uber.dto.*;
import com.chinuthon.project.uber.uber.entities.Ride;
import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.entities.Wallet;
import com.chinuthon.project.uber.uber.entities.enums.PaymentMethod;
import com.chinuthon.project.uber.uber.services.RideService;
import com.chinuthon.project.uber.uber.services.RiderService;
import com.chinuthon.project.uber.uber.services.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiderTools {

    private final RiderService riderService;
    private final RideService rideService;
    private final WalletService walletService;
    private final RideVectorService rideVectorService;

    @Tool(description = "Book a ride/cab for the rider. Requires pickup latitude, pickup longitude, " +
            "dropoff latitude, dropoff longitude, and payment method (CASH or WALLET). " +
            "Coordinates should be in decimal degrees (e.g., latitude: 28.6139, longitude: 77.2090). " +
            "Returns the ride request details including fare and status.")
    public String requestRide(
            @ToolParam(description = "Pickup location latitude (e.g., 28.6139)") double pickupLatitude,
            @ToolParam(description = "Pickup location longitude (e.g., 77.2090)") double pickupLongitude,
            @ToolParam(description = "Dropoff location latitude (e.g., 28.5562)") double dropoffLatitude,
            @ToolParam(description = "Dropoff location longitude (e.g., 77.1000)") double dropoffLongitude,
            @ToolParam(description = "Payment method: CASH or WALLET") String paymentMethod
    ) {
        try {
            log.info("AI Tool: Requesting ride from ({},{}) to ({},{}) with payment: {}",
                    pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude, paymentMethod);

            RideRequestDto rideRequestDto = new RideRequestDto();
            rideRequestDto.setPickUpLocation(new PointDto(new double[]{pickupLongitude, pickupLatitude}));
            rideRequestDto.setDropOffLocation(new PointDto(new double[]{dropoffLongitude, dropoffLatitude}));
            rideRequestDto.setPaymentMethod(PaymentMethod.valueOf(paymentMethod.toUpperCase()));

            RideRequestDto result = riderService.requestRide(rideRequestDto);

            // Store in vector store for semantic search
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String description = String.format(
                "Rider %s booked a ride from coordinates (%.4f, %.4f) to (%.4f, %.4f) for ₹%.2f using %s payment",
                user.getName(), pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude, 
                result.getFare(), paymentMethod
            );
            rideVectorService.storeRideInfo(
                result.getId(), 
                user.getId().toString(), 
                description,
                Map.of(
                    "pickupLat", pickupLatitude,
                    "pickupLon", pickupLongitude,
                    "dropoffLat", dropoffLatitude,
                    "dropoffLon", dropoffLongitude,
                    "fare", result.getFare(),
                    "paymentMethod", paymentMethod
                )
            );

            return String.format("Ride booked successfully! Ride Request ID: %d, Fare: ₹%.2f, Status: %s, Payment: %s",
                    result.getId(), result.getFare(), result.getRideRequestStatus(), result.getPaymentMethod());
        } catch (Exception e) {
            log.error("AI Tool: Error requesting ride", e);
            return "Failed to book ride: " + e.getMessage();
        }
    }

    @Tool(description = "Cancel an existing ride for the rider. Requires the ride ID. " +
            "Only rides with CONFIRMED status can be cancelled.")
    public String cancelRide(
            @ToolParam(description = "The ID of the ride to cancel") Long rideId
    ) {
        try {
            log.info("AI Tool: Cancelling ride with ID: {}", rideId);
            RideDto result = riderService.cancelRide(rideId);
            return String.format("Ride %d cancelled successfully. Status: %s", result.getId(), result.getRideStatus());
        } catch (Exception e) {
            log.error("AI Tool: Error cancelling ride", e);
            return "Failed to cancel ride: " + e.getMessage();
        }
    }

    @Tool(description = "Get the current status and details of a specific ride by its ID.")
    public String getRideStatus(
            @ToolParam(description = "The ID of the ride to check") Long rideId
    ) {
        try {
            log.info("AI Tool: Getting ride status for ID: {}", rideId);
            Ride ride = rideService.getRideById(rideId);
            String driverInfo = ride.getDriver() != null ? "Driver: " + ride.getDriver().getUser().getName() : "No driver assigned yet";

            return String.format("Ride ID: %d, Status: %s, Fare: ₹%.2f, Payment: %s, %s",
                    ride.getId(), ride.getRideStatus(), ride.getFare(), ride.getPaymentMethod(), driverInfo);
        } catch (Exception e) {
            log.error("AI Tool: Error getting ride status", e);
            return "Failed to get ride status: " + e.getMessage();
        }
    }

    @Tool(description = "Get the rider's recent rides history. Returns a list of recent rides with their details.")
    public String getMyRides() {
        try {
            log.info("AI Tool: Getting rider's rides");
            org.springframework.data.domain.PageRequest pageRequest =
                    org.springframework.data.domain.PageRequest.of(0, 5);
            org.springframework.data.domain.Page<RideDto> rides = riderService.getAllMyRides(pageRequest);

            if (rides.isEmpty()) {
                return "You have no rides yet.";
            }

            StringBuilder sb = new StringBuilder("Your recent rides:\n");
            for (RideDto ride : rides.getContent()) {
                sb.append(String.format("- Ride #%d | Status: %s | Fare: ₹%.2f | Payment: %s\n",
                        ride.getId(), ride.getRideStatus(), ride.getFare(), ride.getPaymentMethod()));
            }
            sb.append(String.format("Total rides: %d", rides.getTotalElements()));
            return sb.toString();
        } catch (Exception e) {
            log.error("AI Tool: Error getting rides", e);
            return "Failed to get rides: " + e.getMessage();
        }
    }

    @Tool(description = "Get the rider's profile information including name, email, and rating.")
    public String getMyProfile() {
        try {
            log.info("AI Tool: Getting rider's profile");
            RiderDto profile = riderService.getMyProfile();
            return String.format("Your Profile — Name: %s, Rating: %.1f",
                    profile.getUser().getName(), profile.getRating());
        } catch (Exception e) {
            log.error("AI Tool: Error getting profile", e);
            return "Failed to get profile: " + e.getMessage();
        }
    }

    @Tool(description = "Check the rider's wallet balance.")
    public String getWalletBalance() {
        try {
            log.info("AI Tool: Getting wallet balance");
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Wallet wallet = walletService.findByUser(user);
            return String.format("Your wallet balance: ₹%.2f", wallet.getBalance());
        } catch (Exception e) {
            log.error("AI Tool: Error getting wallet balance", e);
            return "Failed to get wallet balance: " + e.getMessage();
        }
    }

    @Tool(description = "Rate a driver after a completed ride. Requires the ride ID and a rating from 1 to 5. " +
            "Only rides with ENDED status can be rated.")
    public String rateDriver(
            @ToolParam(description = "The ID of the ride") Long rideId,
            @ToolParam(description = "Rating for the driver (1-5)") Integer rating
    ) {
        try {
            log.info("AI Tool: Rating driver for ride {} with rating {}", rideId, rating);
            if (rating < 1 || rating > 5) {
                return "Rating must be between 1 and 5.";
            }
            DriverDto driverDto = riderService.rateDriver(rideId, rating);
            return String.format("Driver rated successfully! Driver's updated rating: %.1f", driverDto.getRating());
        } catch (Exception e) {
            log.error("AI Tool: Error rating driver", e);
            return "Failed to rate driver: " + e.getMessage();
        }
    }
}
