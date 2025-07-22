package com.example.Throws.service;

import com.example.Throws.security.CustomUserDetails;
import com.example.Throws.security.JwtTokenProvider;
import com.example.Throws.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.Throws.dto.auth.request.LoginRequestDTO;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public ResponseEntity<?> login(LoginRequestDTO dto) {
        try {

            // 1. 인증용 토큰 생성 (email + password 입력값 기반)
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());


            // 2. 실제 인증 처리 (UserDetailsService + PasswordEncoder 내부 호출)
            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            // 3. 인증 성공 → JWT 토큰 생성
           String accessToken = jwtTokenProvider.createAccessToken(authentication);
           Member member = ((CustomUserDetails)authentication.getPrincipal()).getMember();
           String refreshToken = tokenService.issueRefreshToken(authentication, member);


            // 4. 응답 반환
            return ResponseEntity.ok(
                    Map.of("accesstoken", accessToken,
                            "refreshtoken", refreshToken));
                    //.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    //.build();

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
        }
    }
}
