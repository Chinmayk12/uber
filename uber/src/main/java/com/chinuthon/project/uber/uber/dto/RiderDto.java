package com.chinuthon.project.uber.uber.dto;

import com.chinuthon.project.uber.uber.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiderDto {
    private User user;
    private Double rating;
}
