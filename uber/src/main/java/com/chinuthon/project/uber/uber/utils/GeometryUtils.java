package com.chinuthon.project.uber.uber.utils;

import com.chinuthon.project.uber.uber.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtils {
    // Static method to convert PointDto to JTS Point
    public static Point createPoint(PointDto pointDto) {
        if (pointDto == null || pointDto.getCoordinates() == null || pointDto.getCoordinates().length < 2) {
            return null;
        }
        // Create a GeometryFactory with SRID 4326 so created geometries carry the correct SRID
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        // Getting the points from Point DTO and creating a Coordinate
        Coordinate coordinate = new Coordinate(pointDto.getCoordinates()[0], pointDto.getCoordinates()[1]);
        Point point = geometryFactory.createPoint(coordinate);
        // Ensure SRID is set (defensive)
        point.setSRID(4326);
        return point;
    }
}
