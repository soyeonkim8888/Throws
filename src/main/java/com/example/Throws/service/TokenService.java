package com.example.Throws.service;

import com.example.Throws.repository.RefreshTokenRepository;
import com.example.Throws.security.JwtTokenProvider;
import com.example.Throws.domain.Member;
import com.example.Throws.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /** 로그인 시 호출: 기존 RT 제거 후 새로 저장 */
    @Transactional
    public String issueRefreshToken(Authentication authentication, Member member) {
        refreshTokenRepository.deleteByMember(member);   // 이미 존재하면 삭제 (로테이션)
        String refresh = jwtTokenProvider.createRefreshToken(authentication);

        RefreshToken entity = RefreshToken.builder()
                .token(refresh)
                .member(member)
                .expiryDate(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpiry()))
                .build();

        refreshTokenRepository.save(entity);
        return refresh;
    }

    /** /api/token/refresh 호출 시 */
    @Transactional
    public String rotateRefreshToken(String oldRefreshToken) {

        RefreshToken saved = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid RefreshToken"));

        if (saved.isExpired())
            throw new IllegalStateException("RefreshToken Expired");

        Member member = saved.getMember();
        Authentication auth = jwtTokenProvider.getAuthentication(oldRefreshToken);

        // 1) 새 Refresh 발급 & 저장
        String newRefresh = jwtTokenProvider.createRefreshToken(auth);
        saved.setToken(newRefresh);                    // 토큰 문자열 교체
        saved.setExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getRefreshExpiry()));

        // 2) 새 Access 발급
        String newAccess = jwtTokenProvider.createAccessToken(auth);

        return newAccess + "::" + newRefresh;          // 간단 구분자 리턴
    }
}