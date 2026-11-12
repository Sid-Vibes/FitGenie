package com.fitnessApplication.controller;


import com.fitnessApplication.dto.response.User.UserResponse;
import com.fitnessApplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;
 //  1. GET USER PROFILE
    @GetMapping("/")
      public ResponseEntity<UserResponse>getProfile(Authentication authentication){
        String email = authentication.getName();
         UserResponse response =userService.getUserProfile(email);
         return ResponseEntity.ok(response);
    }
 // 2. GET USER BMI
    @GetMapping("/bmi")
      public ResponseEntity<?>getBmi(Authentication authentication){
         String email = authentication.getName();
         Double bmi = userService.calculateBMI(email);
         return ResponseEntity.ok(bmi);
    }
}
