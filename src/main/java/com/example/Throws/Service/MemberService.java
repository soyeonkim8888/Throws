package com.example.Throws.Service;

import com.example.Throws.DTO.Register.MemberRegisterRequestDTO;
import com.example.Throws.domain.Member;
import com.example.Throws.Repository.MemberRepository;
import com.example.Throws.Repository.ProviderRepository;
import com.example.Throws.domain.Provider;
import com.example.Throws.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProviderRepository providerRepository;

    // 1. 회원 단건 조회 (ID로)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 2. 전체 회원 리스트 조회
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 3. 회원 정보 수정 (비밀번호, 닉네임 등)
    @Transactional
    public Member updateMember(Long id, String password, String nickname) {
        Member member = findById(id);
        if (password != null && !password.isBlank()) {
            member.setPassword(passwordEncoder.encode(password));
        }
        // 추가 필드 수정 필요시 여기에
        return member;
    }

    // 4. 회원 삭제 (Soft Delete 예시)
    @Transactional
    public void deleteById(Long id) {
        Member member = findById(id);
        member.setDeleted(true); // 실제로 DB에서 삭제하지 않고 상태만 변경
        // 만약 실제 삭제라면 아래 코드 사용
        // memberRepository.deleteById(id);
    }

    // 5. 회원 등록 (일반/소셜 구분)
    @Transactional
    public Member register(MemberRegisterRequestDTO dto) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        Provider provider = providerRepository.findByProviderName(dto.getProviderName())
                .orElseThrow(()-> new IllegalArgumentException("지원하지 않는 소셜 Provider입니다"));

        // provider, providerId도 dto에 있으면 받아서 세팅
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ROLE_USER)
                .provider(provider)// 소셜로그인 provider (ex. KAKAO, GOOGLE 등)
                .providerId(dto.getProviderId())   // 소셜로그인에서 받은 고유 ID
                .build();
        return memberRepository.save(member);
    }

    // 6. 소셜 로그인 회원만 조회
    public List<Member> findByProvider(String provider) {
        return memberRepository.findByProvider_ProviderName(provider);
    }

    // 7. 구독 여부(현재 구독중인지)로 회원 조회
    public List<Member> findSubscribedMembers() {
        return memberRepository.findMembersWithActiveSubscription();
    }

    // 8. 구독 기록이 1번이라도 있는 회원 조회
    public List<Member> findMembersWithSubscriptionHistory() {
        return memberRepository.findMembersWithSubscriptionHistory();
    }

    // 9. 구독 해지한 회원 조회
    public List<Member> findUnsubscribedMembers() {
        return memberRepository.findMembersWithCanceledSubscription();
    }

    // 10. 구독 신청/해지 기간에 따른 회원 조회
    public List<Member> findMembersBySubscriptionPeriod(LocalDate start, LocalDate end, String status) {
        // status: "SUBSCRIBED" or "CANCELED" 등으로 가정
        return memberRepository.findMembersBySubscriptionPeriod(start, end, status);
    }
}