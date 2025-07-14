package com.example.Throws.Controller;

import com.example.Throws.domain.Member;
import com.example.Throws.DTO.Register.MemberRegisterRequestDTO;
import com.example.Throws.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody MemberRegisterRequestDTO dto) {
        Member member = memberService.register(dto);
        return ResponseEntity.ok(member);
    }
}
