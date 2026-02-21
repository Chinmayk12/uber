package com.chinuthon.project.uber.uber.services;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.SignupDto;
import com.chinuthon.project.uber.uber.dto.UserDto;

public interface AuthService {
    String login(String email, String password);
    UserDto signup(SignupDto signupDto);
    DriverDto onboardDriver(Long userId);
}
