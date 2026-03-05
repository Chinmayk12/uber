package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.Security.JwtService;
import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.SignupDto;
import com.chinuthon.project.uber.uber.dto.UserDto;
import com.chinuthon.project.uber.uber.entities.Driver;
import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.entities.enums.Role;
import com.chinuthon.project.uber.uber.exceptions.ResourceNotFoundException;
import com.chinuthon.project.uber.uber.exceptions.RuntimeConflictException;
import com.chinuthon.project.uber.uber.repositories.UserRepository;
import com.chinuthon.project.uber.uber.services.AuthService;
import com.chinuthon.project.uber.uber.services.DriverService;
import com.chinuthon.project.uber.uber.services.RiderService;
import com.chinuthon.project.uber.uber.services.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;
    private final WalletService walletService;
    private final DriverService driverService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public String[] login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new String [] {accessToken,refreshToken};
    }

    @Override
    @Transactional
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.findByEmail(signupDto.getEmail()).orElse(null);

        if (user != null) {
            throw new RuntimeConflictException(
                    "Cannot Signup,User with email " + signupDto.getEmail() + " already exists");
        }

        // Mapping SignupDto to User Entity
        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        mappedUser.setPassword(passwordEncoder.encode(mappedUser.getPassword()));
        User savedUser = userRepository.save(mappedUser);

        // Create User Related Entities
        riderService.createNewRider(savedUser);
        walletService.createNewWallet(savedUser);

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public DriverDto onboardDriver(Long userId,String vehicleId) {
        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("User not found with id "+userId));

        if(user.getRoles().contains(Role.DRIVER))
            throw new RuntimeConflictException("User with id "+userId+"is already a Driver");

        Driver createDriver = Driver.builder()
                .user(user)
                .rating(0.0)
                .vehicleId(vehicleId)
                .available(true)
                .build();

        user.getRoles().add(Role.DRIVER);
        userRepository.save(user);
        Driver savedDriver = driverService.createNewDriver(createDriver);
        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public String refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("User Not Found With Id: "+userId));

        return jwtService.generateAccessToken(user);
    }
}
