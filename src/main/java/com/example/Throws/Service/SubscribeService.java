package com.example.Throws.Service;

import com.example.Throws.domain.*;
import com.example.Throws.Repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final SubscribeRepository subscribeRepository;

    @Transactional
    public Subscribe startTrial(Member member) {
        // 이미 ACTIVE 또는 TRIAL 이력이 있으면 방지(중복체험 불가)
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);
        if (latest.isPresent() &&
                (latest.get().getStatus() == SubscribeStatus.TRIAL || latest.get().getStatus() == SubscribeStatus.ACTIVE)) {
            throw new IllegalStateException("이미 체험 또는 구독중입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        Subscribe trial = Subscribe.builder()
                .member(member)
                .startDate(now)
                .endDate(now.plusDays(1))  // 24시간
                .status(SubscribeStatus.TRIAL)
                .isTrial(true)
                .price(0)
                .build();

        return subscribeRepository.save(trial);
    }

    // 2. 구독 결제 (언제든 가능, 무료체험 중에도 바로 전환)
    @Transactional
    public Subscribe startPaidSubscribe(Member member, String paymentProvider, String paymentId, int price) {
        // 구독중이면 기간 연장 or 새로 시작 정책에 따라 처리
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newStartDate = now;
        LocalDateTime newEndDate = now.plusMonths(1);

        // 무료체험 중이면 그 구독 레코드를 유료로 업데이트
        if (latest.isPresent() && latest.get().getStatus() == SubscribeStatus.TRIAL) {
            Subscribe sub = latest.get();
            sub.setStatus(SubscribeStatus.ACTIVE);
            sub.setIsTrial(false);
            sub.setStartDate(now);
            sub.setEndDate(newEndDate);
            sub.setPaymentProvider(paymentProvider);
            sub.setPaymentId(paymentId);
            sub.setPrice(price);
            return subscribeRepository.save(sub);
        } else if (latest.isPresent() && latest.get().getStatus() == SubscribeStatus.ACTIVE
                && latest.get().getEndDate().isAfter(now)) {
            // 유료 구독중이면 "기간연장"
            Subscribe sub = latest.get();
            sub.setEndDate(sub.getEndDate().plusMonths(1));
            // 결제정보, price 등 필요시 갱신
            sub.setPaymentProvider(paymentProvider);
            sub.setPaymentId(paymentId);
            sub.setPrice(sub.getPrice() + price); // 누적금액 (옵션)
            return subscribeRepository.save(sub);
        } else {
            // 그 외엔 새로 시작
            Subscribe sub = Subscribe.builder()
                    .member(member)
                    .startDate(newStartDate)
                    .endDate(newEndDate)
                    .status(SubscribeStatus.ACTIVE)
                    .isTrial(false)
                    .paymentProvider(paymentProvider)
                    .paymentId(paymentId)
                    .price(price)
                    .build();
            return subscribeRepository.save(sub);
        }
    }

    // 3. 구독 상태 조회
    @Transactional(readOnly = true)
    public SubscribeStatus checkSubscribeStatus(Member member) {
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);
        if (latest.isEmpty()) return SubscribeStatus.EXPIRED;

        Subscribe sub = latest.get();
        LocalDateTime now = LocalDateTime.now();

        if (sub.getStatus() == SubscribeStatus.CANCELED && sub.getEndDate().isBefore(now)) {
            return SubscribeStatus.EXPIRED;
        }
        if (sub.getEndDate().isBefore(now)) {
            return SubscribeStatus.EXPIRED;
        }
        return sub.getStatus();
    }

    // 4. 구독 해지 (취소)
    @Transactional
    public void cancelSubscribe(Member member) {
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);
        if (latest.isEmpty() || latest.get().getStatus() != SubscribeStatus.ACTIVE) {
            throw new IllegalStateException("구독 중이 아닙니다.");
        }
        Subscribe sub = latest.get();
        sub.setStatus(SubscribeStatus.CANCELED);
        sub.setCanceledAt(LocalDateTime.now());
        subscribeRepository.save(sub);
    }

    // 5. 환불 (7일 이내 전액환불)
    @Transactional
    public void refundSubscribe(Member member) {
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);
        if (latest.isEmpty() || latest.get().getStatus() != SubscribeStatus.ACTIVE) {
            throw new IllegalStateException("구독중이 아닙니다.");
        }
        Subscribe sub = latest.get();
        LocalDateTime now = LocalDateTime.now();
        if (sub.getStartDate().plusDays(7).isBefore(now)) {
            throw new IllegalStateException("환불 가능 기간이 지났습니다.");
        }
        sub.setStatus(SubscribeStatus.REFUNDED);
        sub.setRefundedAt(now);
        // 서비스 즉시 중단 (endDate = now 등 필요시 처리)
        sub.setEndDate(now);
        subscribeRepository.save(sub);
        // (결제사 연동 코드 필요시 추가)
    }
}
