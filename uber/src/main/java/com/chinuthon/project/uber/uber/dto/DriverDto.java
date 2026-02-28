package com.chinuthon.project.uber.uber.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long id;
    private UserDto user;
    private Boolean available;
    private String vehicleId;
    private Double rating;

}
