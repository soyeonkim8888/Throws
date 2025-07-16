package com.example.Throws.Controller;

import com.example.Throws.Service.MemberService;
import com.example.Throws.Service.SubscribeService;
import com.example.Throws.domain.Member;
import com.example.Throws.domain.Subscribe;
import com.example.Throws.domain.SubscribeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import com.example.Throws.DTO.Subscribe.PaymentRequestDTO;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {
    private final SubscribeService subscribeService;
    private final MemberService memberService;

    // 1. 무료체험 시작
    @PostMapping("/trial")
    public Subscribe startTrial(@RequestBody Member member) {
        return subscribeService.startTrial(member);
    }

    // 2. 구독 결제(신용카드/페이팔)
    @PostMapping("/payment")
    public Subscribe startPaid(@RequestBody PaymentRequestDTO dto) {
        // dto: { memberId, paymentProvider, paymentId, price }
        Member member= memberService.findById(dto.getMemberId());
        return subscribeService.startPaidSubscribe(member, dto.getPaymentProvider(), dto.getPaymentId(), dto.getPrice(), dto.getAutoRenewal());
    }

    // 3. 구독 상태 확인
    @GetMapping("/status")
    public SubscribeStatus getStatus(@RequestBody Member member) {
        return subscribeService.checkSubscribeStatus(member);
    }

    // 4. 해지
    @PostMapping("/cancel")
    public String cancel(@RequestBody Member member) {
        subscribeService.cancelSubscribe(member);
        return "구독이 해지되었습니다.";
    }

    // 5. 환불
    @PostMapping("/refund")
    public String refund(@RequestBody Member member) {
        subscribeService.refundSubscribe(member);
        return "환불이 완료되었습니다.";
    }

}
