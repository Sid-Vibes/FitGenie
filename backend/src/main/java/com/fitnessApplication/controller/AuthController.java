package com.fitnessApplication.controller;

import com.fitnessApplication.dto.request.User.RefreshTokenRequest;
import com.fitnessApplication.dto.request.User.UserLoginRequest;
import com.fitnessApplication.dto.request.User.UserRegistrationRequest;
import com.fitnessApplication.dto.response.User.RefreshTokenResponse;
import com.fitnessApplication.dto.response.User.UserAuthResponse;
import com.fitnessApplication.dto.response.User.UserResponse;
import com.fitnessApplication.model.RefreshToken;
import com.fitnessApplication.service.AuthService;
import com.fitnessApplication.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;
    @Autowired
    private  AuthenticationManager authenticationManager;

    private  final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegistrationRequest request) {

          UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(
            @Valid @RequestBody UserLoginRequest request) {

        String email = request.email();
        String password = request.password();
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password));
        String username = authentication.getName();
        UserResponse user = authService.getuserByemail(username);
        String token = authService.generateToken(email);
        String refreshtoken = authService.generateRefreshToken(email);
        UserAuthResponse response = getUser(user,token,refreshtoken);
        return  ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        RefreshToken storedToken =
                refreshTokenService.validateRefreshToken(
                        request.refreshToken()
                );

        String email = storedToken.getEmail();

        String newAccessToken = authService.generateToken(email);

        RefreshTokenResponse response = new RefreshTokenResponse(
                newAccessToken,
                "Bearer"
        );
        return ResponseEntity.ok(response);
    }


    private  UserAuthResponse getUser(UserResponse user,String token,String refreshtoken){
        return  UserAuthResponse.builder()
                .accessToken(token)
                .refreshToken(refreshtoken)
                .tokenType("Bearer")
                .user(user)
                .build();
    }
}
