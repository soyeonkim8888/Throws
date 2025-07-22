package com.example.Throws.service;

import com.example.Throws.domain.*;
import com.example.Throws.repository.SubscribeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public Subscribe startPaidSubscribe(Member member, String paymentProvider, String paymentId, int price, boolean autoRenewal) {
        // 구독중이면 기간 연장 or 새로 시작 정책에 따라 처리
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newStartDate = now;
        LocalDateTime newEndDate = now.plusDays(1);

        int renewalCount = 1;

        if (latest.isPresent()) {
            Subscribe sub = latest.get();

            // 이전 구독의 renewalCount 가져오기
            renewalCount = sub.getRenewalCount() + 1;

            // 무료체험 중이면 유료로 전환 (갱신1회)
            if (sub.getStatus() == SubscribeStatus.TRIAL) {
                sub.setStatus(SubscribeStatus.ACTIVE);
                sub.setIsTrial(false);
                sub.setStartDate(now);
                sub.setEndDate(newEndDate);
                sub.setPaymentProvider(paymentProvider);
                sub.setPaymentId(paymentId);
                sub.setPrice(price);
                sub.setAutoRenewal(autoRenewal);
                sub.setRenewalCount(renewalCount);
                return subscribeRepository.save(sub);
            }
            // 유료 구독중이고 아직 만료 전: 자동갱신 여부 체크
            if (sub.getStatus() == SubscribeStatus.ACTIVE && sub.getEndDate().isAfter(now)) {
                if (sub.getAutoRenewal() != null && sub.getAutoRenewal()) {
                    // 자동갱신 → 바로 연장
                    sub.setEndDate(sub.getEndDate().plusMonths(1));
                    sub.setPaymentProvider(paymentProvider);
                    sub.setPaymentId(paymentId);
                    sub.setPrice(price);
                    sub.setRenewalCount(renewalCount);
                    return subscribeRepository.save(sub);
                } else {
                    // 자동갱신 미신청 → 결제일에 별도 승인 로직 필요(여기선 안내만)
                    throw new IllegalStateException("자동갱신 미신청 상태입니다. 결제일에 결제 여부를 확인하세요.");
                }
            }
        }

        // 최초 유료 구독 시작
        Subscribe sub = Subscribe.builder()
                .member(member)
                .startDate(now)
                .endDate(newEndDate)
                .status(SubscribeStatus.ACTIVE)
                .isTrial(false)
                .paymentProvider(paymentProvider)
                .paymentId(paymentId)
                .price(price)
                .autoRenewal(Boolean.TRUE.equals(autoRenewal))
                .renewalCount(renewalCount)
                .build();
        return subscribeRepository.save(sub);
    }

    // 2. 결제일에 자동갱신 미신청 상태일 때, 결제 승인 확인(프론트 연동용)
    public boolean needPaymentApproval(Member member) {
        Optional<Subscribe> latest = subscribeRepository.findFirstByMemberOrderByEndDateDesc(member);
        if (latest.isPresent()) {
            Subscribe sub = latest.get();
            // 만료 임박 && 자동갱신 X
            LocalDateTime now = LocalDateTime.now();
            if (!Boolean.TRUE.equals(sub.getAutoRenewal())
                    && sub.getEndDate().isAfter(now)
                    && sub.getEndDate().minusDays(1).isBefore(now)) {
                return true;
            }
        }
        return false;
    }

    // 3. 누적 구독횟수 반환
    public int getTotalRenewalCount(Member member) {
        List<Subscribe> subs = subscribeRepository.findByMember(member);
        return subs.stream().mapToInt(Subscribe::getRenewalCount).max().orElse(0);
    }

    // 4. 구독 상태 조회
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

    // 5. 구독 해지 (취소)
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

    // 6. 환불 (7일 이내 전액환불)
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
