package com.example.Throws.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;               // 실제 Refresh-JWT

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;              // 1:1 대응

    @Column(nullable = false)
    private Instant expiryDate;         // 만료 시각

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
