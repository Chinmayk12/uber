package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.services.DistanceService;
import org.locationtech.jts.geom.Point;

public class DistanceServiceOSRMImpl implements DistanceService {
    @Override
    public double calculateDistance(Point src, Point dest) {
        // TODO Will make an 3rd party API call to OSRM to get the distance between the two points
        return 0;
    }
}
