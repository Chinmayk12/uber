package com.chinuthon.project.uber.uber.dto;

import lombok.Data;

@Data
public class PointDto {
    private double[] coordinates; // [latitude, longitude]
    private String type = "Point"; // e.g., "Point"
}
