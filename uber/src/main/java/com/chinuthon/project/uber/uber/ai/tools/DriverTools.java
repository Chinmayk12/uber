package com.chinuthon.project.uber.uber.ai.tools;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.RideDto;
import com.chinuthon.project.uber.uber.dto.RiderDto;
import com.chinuthon.project.uber.uber.services.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverTools {

    private final DriverService driverService;

    @Tool(description = "Accept a ride request as a driver. Requires the ride request ID. " +
            "This will assign the driver to the ride and notify the rider.")
    public String acceptRide(
            @ToolParam(description = "The ID of the ride request to accept") Long rideRequestId
    ) {
        try {
            log.info("AI Tool: Driver accepting ride request ID: {}", rideRequestId);
            RideDto result = driverService.acceptRide(rideRequestId);
            return String.format("Ride request accepted! Ride ID: %d, Fare: ₹%.2f, Status: %s, OTP: %s",
                    result.getId(), result.getFare(), result.getRideStatus(), result.getOtp());
        } catch (Exception e) {
            log.error("AI Tool: Error accepting ride", e);
            return "Failed to accept ride: " + e.getMessage();
        }
    }

    @Tool(description = "Start a ride as a driver. Requires the ride request ID and the OTP provided by the rider. " +
            "The OTP must match the one generated when the ride was accepted.")
    public String startRide(
            @ToolParam(description = "The ID of the ride request to start") Long rideRequestId,
            @ToolParam(description = "The OTP provided by the rider") String otp
    ) {
        try {
            log.info("AI Tool: Driver starting ride ID: {} with OTP: {}", rideRequestId, otp);
            RideDto result = driverService.startRide(rideRequestId, otp);
            return String.format("Ride started successfully! Ride ID: %d, Status: %s, Fare: ₹%.2f",
                    result.getId(), result.getRideStatus(), result.getFare());
        } catch (Exception e) {
            log.error("AI Tool: Error starting ride", e);
            return "Failed to start ride: " + e.getMessage();
        }
    }

    @Tool(description = "End a ride as a driver. Requires the ride ID. " +
            "This will complete the ride and process the payment.")
    public String endRide(
            @ToolParam(description = "The ID of the ride to end") Long rideId
    ) {
        try {
            log.info("AI Tool: Driver ending ride ID: {}", rideId);
            RideDto result = driverService.endRide(rideId);
            return String.format("Ride ended successfully! Ride ID: %d, Status: %s, Fare: ₹%.2f, Payment: %s",
                    result.getId(), result.getRideStatus(), result.getFare(), result.getPaymentMethod());
        } catch (Exception e) {
            log.error("AI Tool: Error ending ride", e);
            return "Failed to end ride: " + e.getMessage();
        }
    }

    @Tool(description = "Cancel a ride as a driver. Requires the ride ID. " +
            "Only rides that haven't been completed can be cancelled.")
    public String cancelRideAsDriver(
            @ToolParam(description = "The ID of the ride to cancel") Long rideId
    ) {
        try {
            log.info("AI Tool: Driver cancelling ride ID: {}", rideId);
            RideDto result = driverService.cancelRide(rideId);
            return String.format("Ride cancelled successfully. Ride ID: %d, Status: %s", 
                    result.getId(), result.getRideStatus());
        } catch (Exception e) {
            log.error("AI Tool: Error cancelling ride", e);
            return "Failed to cancel ride: " + e.getMessage();
        }
    }

    @Tool(description = "Rate a rider after a completed ride. Requires the ride ID and a rating from 1 to 5. " +
            "Only rides with ENDED status can be rated.")
    public String rateRider(
            @ToolParam(description = "The ID of the ride") Long rideId,
            @ToolParam(description = "Rating for the rider (1-5)") Integer rating
    ) {
        try {
            log.info("AI Tool: Driver rating rider for ride {} with rating {}", rideId, rating);
            if (rating < 1 || rating > 5) {
                return "Rating must be between 1 and 5.";
            }
            RiderDto riderDto = driverService.rateRider(rideId, rating);
            return String.format("Rider rated successfully! Rider's updated rating: %.1f", riderDto.getRating());
        } catch (Exception e) {
            log.error("AI Tool: Error rating rider", e);
            return "Failed to rate rider: " + e.getMessage();
        }
    }

    @Tool(description = "Get the driver's profile information including name, rating, vehicle ID, and availability status.")
    public String getDriverProfile() {
        try {
            log.info("AI Tool: Getting driver's profile");
            DriverDto profile = driverService.getMyProfile();
            return String.format("Your Profile — Name: %s, Rating: %.1f, Vehicle: %s, Available: %s",
                    profile.getUser().getName(), profile.getRating(), 
                    profile.getVehicleId(), profile.getAvailable() ? "Yes" : "No");
        } catch (Exception e) {
            log.error("AI Tool: Error getting driver profile", e);
            return "Failed to get profile: " + e.getMessage();
        }
    }

    @Tool(description = "Get the driver's ride history. Returns a list of recent rides with their details.")
    public String getDriverRides() {
        try {
            log.info("AI Tool: Getting driver's rides");
            PageRequest pageRequest = PageRequest.of(0, 5, 
                    Sort.by(Sort.Direction.DESC, "createdTime", "id"));
            Page<RideDto> rides = driverService.getAllMyRides(pageRequest);

            if (rides.isEmpty()) {
                return "You have no rides yet.";
            }

            StringBuilder sb = new StringBuilder("Your recent rides:\n");
            for (RideDto ride : rides.getContent()) {
                String riderName = ride.getRider() != null ? ride.getRider().getUser().getName() : "Unknown";
                sb.append(String.format("- Ride #%d | Rider: %s | Status: %s | Fare: ₹%.2f | Payment: %s\n",
                        ride.getId(), riderName, ride.getRideStatus(), ride.getFare(), ride.getPaymentMethod()));
            }
            sb.append(String.format("Total rides: %d", rides.getTotalElements()));
            return sb.toString();
        } catch (Exception e) {
            log.error("AI Tool: Error getting driver rides", e);
            return "Failed to get rides: " + e.getMessage();
        }
    }

    @Tool(description = "Update the driver's availability status. Set to true to go online and accept rides, " +
            "or false to go offline and stop receiving ride requests.")
    public String updateAvailability(
            @ToolParam(description = "Availability status: true to go online, false to go offline") Boolean available
    ) {
        try {
            log.info("AI Tool: Updating driver availability to: {}", available);
            DriverDto currentProfile = driverService.getMyProfile();
            driverService.updateDriverAvailability(driverService.getCurrentDriver(), available);
            return String.format("Availability updated successfully! You are now %s.", 
                    available ? "ONLINE and ready to accept rides" : "OFFLINE");
        } catch (Exception e) {
            log.error("AI Tool: Error updating availability", e);
            return "Failed to update availability: " + e.getMessage();
        }
    }
}
