package com.example.Throws.Repository;

import com.example.Throws.domain.Member;
import com.example.Throws.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByMember(String member);
    void deleteByMember(Member member);
}
