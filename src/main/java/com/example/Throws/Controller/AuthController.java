package com.example.Throws.Controller;

import com.example.Throws.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Throws.DTO.Login.LoginRequestDTO;
import com.example.Throws.DTO.Login.RefreshTokenRequestDTO;
import com.example.Throws.DTO.Login.TokenResponseDTO;
import com.example.Throws.Service.TokenService;

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
