package com.chinuthon.project.uber.uber.controllers;

import com.chinuthon.project.uber.uber.dto.SignupDto;
import com.chinuthon.project.uber.uber.dto.UserDto;
import com.chinuthon.project.uber.uber.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    UserDto signup(@RequestBody SignupDto signupDto){
        return authService.signup(signupDto);
    }
}
