package com.chinuthon.project.uber.uber.stategies;

import com.chinuthon.project.uber.uber.stategies.impl.DriverMatchingHighestRatedDriverStrategy;
import com.chinuthon.project.uber.uber.stategies.impl.DriverMatchingNearestDriverStrategy;
import com.chinuthon.project.uber.uber.stategies.impl.RideFareDefaultFareCalculationStrategy;
import com.chinuthon.project.uber.uber.stategies.impl.RideFareSergePricingFareCalculationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

// This Class is responsible for managing the different strategies for driver matching and fare calculation based on certain conditions like rider rating and time of day.
@Component
@RequiredArgsConstructor
public class RideStrategyManager {
    private final DriverMatchingHighestRatedDriverStrategy highestRatedDriverStrategy;
    private final DriverMatchingNearestDriverStrategy nearestDriverStrategy;
    private final RideFareDefaultFareCalculationStrategy defaultFareCalculationStrategy;
    private final RideFareSergePricingFareCalculationStrategy sergePricingFareCalculationStrategy;

    public DriverMatchingStrategy driverMatchingStrategy(double riderRating) {
        if (riderRating >= 4.5) {
            return highestRatedDriverStrategy;
        } else {
            return nearestDriverStrategy;
        }
    }

    public RideFareCalculationStrategy rideFareCalculationStrategy() {
        // 6 PM to 9 PM is considered peak hours for surge pricing

        LocalTime surgeStartTime = LocalTime.of(18, 0);
        LocalTime surgeEndTime = LocalTime.of(21, 0);

        boolean isSurgePricing = LocalTime.now().isAfter(surgeStartTime) && LocalTime.now().isBefore(surgeEndTime);

        if (isSurgePricing) {
            return sergePricingFareCalculationStrategy;
        } else {
            return defaultFareCalculationStrategy;
        }

    }
}
