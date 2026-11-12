package com.fitnessApplication.service;

import com.fitnessApplication.dto.response.User.UserResponse;
import com.fitnessApplication.exception.ResourceNotFoundException;
import com.fitnessApplication.model.User;
import com.fitnessApplication.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private  final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserResponse getUserProfile(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException(
                         "User not found"
                ));
        return authService.toUserResponse(user);
    }

    public Double calculateBMI(String email){
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException(
                         "User are not found"
                ));
        return user.getWeightKg()/ user.getHeightCm()*user.getHeightCm()*10000;
    }

}
