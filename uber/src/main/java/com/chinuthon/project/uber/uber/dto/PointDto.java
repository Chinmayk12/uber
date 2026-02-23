package com.chinuthon.project.uber.uber.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PointDto {
    private String type = "Point"; // e.g., "Point"
    private double[] coordinates;

    public PointDto(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
