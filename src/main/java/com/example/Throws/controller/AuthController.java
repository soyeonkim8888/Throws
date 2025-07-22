package com.example.Throws.controller;

import com.example.Throws.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Throws.dto.auth.request.LoginRequestDTO;
import com.example.Throws.dto.auth.request.RefreshTokenRequestDTO;
import com.example.Throws.service.TokenService;

import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDto) {
        return authService.login(loginRequestDto);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequestDTO dto) {

        String combined = tokenService.rotateRefreshToken(dto.getRefreshToken());
        String[] parts  = combined.split("::");

        return ResponseEntity.ok(
                Map.of("accessToken",  parts[0],
                        "refreshToken", parts[1])      // RT 로테이션
        );
    }
}
