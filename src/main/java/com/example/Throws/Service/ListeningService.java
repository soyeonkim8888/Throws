package com.example.Throws.Service;

import com.example.Throws.Repository.MemberRepository;
import com.example.Throws.Repository.SubscribeRepository;
import com.example.Throws.domain.Subscribe;
import com.example.Throws.domain.SubscribeStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListeningService {
    private final SubscribeRepository subscribeRepository;
    private final MemberRepository memberRepository;

    public boolean canAccessListening(Long memberId) {
        // 최신 구독 기록 조회 (무료체험/유료 구분 없이)
        Optional<Subscribe> latestSubscribe =
                subscribeRepository.findFirstByMemberOrderByEndDateDesc(memberRepository.getReferenceById(memberId));
        if (latestSubscribe.isPresent()) {
            Subscribe subscribe = latestSubscribe.get();
            // 오늘 날짜 기준 endDate 이후면 만료
            return subscribe.getEndDate().isAfter(LocalDateTime.now())
                    && (subscribe.getStatus() == SubscribeStatus.ACTIVE || subscribe.getStatus() == SubscribeStatus.TRIAL);
        }
        return false;
    }

    // 실제 리스닝 데이터 제공 (샘플)
    public String getListeningContent(Long memberId) {
        if (!canAccessListening(memberId)) {
            throw new AccessDeniedException("구독이 필요합니다.");
        }
        // 실제 리스닝 데이터 로직
        return "프리미엄 리스닝 데이터입니다.";
    }
}
