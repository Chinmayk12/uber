package com.chinuthon.project.uber.uber.dto;

import com.chinuthon.project.uber.uber.entities.User;
import jakarta.annotation.sql.DataSourceDefinitions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private UserDto user;
    private Double rating;
}
