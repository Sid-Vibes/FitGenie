package com.fitnessApplication.service;

import com.fitnessApplication.dto.request.User.UserRegistrationRequest;
import com.fitnessApplication.dto.response.User.UserResponse;
import com.fitnessApplication.model.RefreshToken;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.UserRepository;
import com.fitnessApplication.utils.jwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private jwtUtils jwtutils;
    @Autowired
    private RefreshTokenService refreshTokenService;
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(UserRegistrationRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());

        // Never store a plain-text password.
        user.setPassword(
                passwordEncoder.encode(request.password()));

        user.setAge(request.age());
        user.setHeightCm(request.heightCm());
        user.setRole("ROLE_USER");
        user.setWeightKg(request.weightKg());
        user.setFitnessGoal(request.fitnessGoal());
        user.setActivityLevel(request.activityLevel());

        User savedUser = userRepository.save(user);

        return  toUserResponse(savedUser);
    }

    public UserResponse toUserResponse(User saveduser){
         return  UserResponse.builder()
                 .id(saveduser.getId())
                 .name(saveduser.getName())
                 .email(saveduser.getEmail())
                 .age(saveduser.getAge())
                 .weightKg(saveduser.getWeightKg())
                 .activityLevel(saveduser.getActivityLevel())
                 .heightCm(saveduser.getHeightCm())
                 .fitnessGoal(saveduser.getFitnessGoal())
                 .createdAt(saveduser.getCreatedAt())
                 .build();
    }

    public String generateToken(String email){
         return jwtutils.generateToken(email);
    }

    public UserResponse getuserByemail(String email){
         return toUserResponse(userRepository.findByEmail(email).orElse(null));
    }

    public String generateRefreshToken(String email) {
        return refreshTokenService.createRefreshToken(email);
    }

    public String refreshJwtToken(String refreshToken) {
        RefreshToken token = refreshTokenService.validateRefreshToken(refreshToken);
        return generateToken(token.getEmail());
    }

}
