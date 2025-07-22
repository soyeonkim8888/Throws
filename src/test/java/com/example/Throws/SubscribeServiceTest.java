package com.example.Throws;
import com.example.Throws.service.SubscribeService;
import com.example.Throws.domain.*;
import com.example.Throws.repository.MemberRepository;
import com.example.Throws.repository.SubscribeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.Throws.domain.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class SubscribeServiceTest {
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private SubscribeRepository subscribeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .email("test@example.com")
                .password("abcd1234")
                        .role(ROLE_USER)
                .build());
    }

    @Test
    void 무료체험_시작() {
        Subscribe sub = subscribeService.startTrial(member);

        assertThat(sub.getStatus()).isEqualTo(SubscribeStatus.TRIAL);
        assertThat(sub.getIsTrial()).isTrue();
        assertThat(sub.getPrice()).isZero();
    }

    @Test
    void 구독_결제() {
        // 먼저 무료체험
        subscribeService.startTrial(member);

        // 결제 요청
        Subscribe sub = subscribeService.startPaidSubscribe(
                member, "paypal", "pay_1234", 10);

        assertThat(sub.getStatus()).isEqualTo(SubscribeStatus.ACTIVE);
        assertThat(sub.getPaymentProvider()).isEqualTo("paypal");
        assertThat(sub.getIsTrial()).isFalse();
        assertThat(sub.getPrice()).isEqualTo(10);
    }

    @Test
    void 구독_해지() {
        subscribeService.startPaidSubscribe(member, "stripe", "pay_5678", 10);
        subscribeService.cancelSubscribe(member);

        Subscribe latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member).get();
        assertThat(latest.getStatus()).isEqualTo(SubscribeStatus.CANCELED);
        assertThat(latest.getCanceledAt()).isNotNull();
    }

    @Test
    void 구독_환불() {
        subscribeService.startPaidSubscribe(member, "stripe", "pay_9999", 10);

        subscribeService.refundSubscribe(member);

        Subscribe latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member).get();
        assertThat(latest.getStatus()).isEqualTo(SubscribeStatus.REFUNDED);
        assertThat(latest.getRefundedAt()).isNotNull();
        assertThat(latest.getEndDate()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void 상태_조회() {
        subscribeService.startPaidSubscribe(member, "stripe", "pay_1111", 10);

        SubscribeStatus status = subscribeService.checkSubscribeStatus(member);

        assertThat(status).isEqualTo(SubscribeStatus.ACTIVE);
    }
}
