package com.example.Throws.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscribeStatus status; // 구독상태 (TRIAL, ACTIVE, CANCELED, EXPIRED, REFUNDED 등)

    @Column(nullable = true)
    private LocalDateTime canceledAt; // 해지 요청 시각 (nullable)

    @Column(nullable = true)
    private LocalDateTime refundedAt; // 환불 시각 (nullable)

    @Column(nullable = false)
    private Boolean isTrial; // 무료체험 여부

    @Column(nullable = true)
    private String paymentProvider; // stripe, paypal 등 (nullable: 무료체험은 null)

    @Column(nullable = true)
    private String paymentId; // 결제 고유 ID(외부 결제사와 연동시 필요, 무료체험은 null)

    @Column(nullable = false)
    private int price; // 실제 결제금액(USD, 무료체험은 0)

    // ✅ 자동갱신 여부 추가
    @Column(nullable = false)
    private Boolean autoRenewal;

    // ✅ 누적 구독 횟수
    @Column(nullable = false)
    private int renewalCount;


}
