package com.example.Throws.Service;

import com.example.Throws.DTO.Register.MemberRegisterRequestDTO;
import com.example.Throws.domain.Member;
import com.example.Throws.Repository.MemberRepository;
import com.example.Throws.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    // 1. 회원 단건 조회 (ID로)
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    // 2. 전체 회원 리스트 조회 (옵션)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // 3. 회원 저장 (옵션)
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    // 4. 회원 삭제 (옵션)
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Transactional
    public Member register(MemberRegisterRequestDTO dto) {
        // provider 연동은 생략, 필요시 추가
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(Role.ROLE_USER)      // ⭐️ 반드시 role 지정!
                .build();
        return memberRepository.save(member);
    }
}
