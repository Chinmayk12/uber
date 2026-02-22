package com.chinuthon.project.uber.uber.services.impl;

import com.chinuthon.project.uber.uber.dto.DriverDto;
import com.chinuthon.project.uber.uber.dto.SignupDto;
import com.chinuthon.project.uber.uber.dto.UserDto;
import com.chinuthon.project.uber.uber.entities.User;
import com.chinuthon.project.uber.uber.entities.enums.Role;
import com.chinuthon.project.uber.uber.exceptions.RuntimeConflictException;
import com.chinuthon.project.uber.uber.repositories.UserRepository;
import com.chinuthon.project.uber.uber.services.AuthService;
import com.chinuthon.project.uber.uber.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RiderService riderService;

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public UserDto signup(SignupDto signupDto) {
        User user = userRepository.
                    findByEmail(signupDto.getEmail()).
                    orElseThrow(()-> new RuntimeConflictException("User with email already exists"));

        // Mapping SignupDto to User Entity
        User mappedUser = modelMapper.map(signupDto, User.class);
        mappedUser.setRoles(Set.of(Role.RIDER));
        User savedUser = userRepository.save(mappedUser);

        // Create User Related Entities
        riderService.createNewRider(savedUser);

        // TODO Add the wallet related servie here


        return null;
    }

    @Override
    public DriverDto onboardDriver(Long userId) {
        return null;
    }
}
