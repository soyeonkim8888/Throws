package com.example.Throws.Security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}") //lombok.Value랑 헷갈리면 안됨. 어노테이션 걸때 잘 확인해야함.
    private String secret;

    @Value("${jwt.access-exp}")
    private long accessExpiry;

    @Value("${jwt.refresh-exp}")
    private long refreshExpiry;

    public long getRefreshExpiry() {   // ← Getter 추가
        return refreshExpiry;
    }

    private Key key;
    private final CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /*
    public String createToken(String email, Collection<? extends GrantedAuthority> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + 3600_000); // 1시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
 */

    public String createAccessToken(Authentication auth) {
        return buildToken(auth.getName(), auth.getAuthorities(), accessExpiry);
    }

    public String createRefreshToken(Authentication auth) {
        // Refresh에는 권한 정보 불필요 → 빈 리스트
        return buildToken(auth.getName(), List.of(), refreshExpiry);
    }

    private String buildToken(String email,
                              Collection<? extends GrantedAuthority> roles,
                              long ttlMillis) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles.stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ttlMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String email = getUserEmail(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
