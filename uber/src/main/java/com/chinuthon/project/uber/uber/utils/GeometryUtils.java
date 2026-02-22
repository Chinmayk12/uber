package com.chinuthon.project.uber.uber.utils;

import com.chinuthon.project.uber.uber.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GeometryUtils {
    // Static method to convert PointDto to JTS Point
    public static Point createPoint(PointDto pointDto) {
        // Assuming you have a GeometryFactory instance available
        GeometryFactory geometryFactory = new GeometryFactory();

        // Getting the points from Point DTO and creating a Coordinate
        Coordinate coordinate = new Coordinate(pointDto.getCoordinates()[0],pointDto.getCoordinates()[1]);
        return geometryFactory.createPoint(coordinate);
    }
}
