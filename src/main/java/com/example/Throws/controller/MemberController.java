package com.example.Throws.controller;

import com.example.Throws.domain.Member;
import com.example.Throws.dto.auth.request.MemberRegisterRequestDTO;
import com.example.Throws.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody MemberRegisterRequestDTO dto) {
        Member member = memberService.register(dto);
        return ResponseEntity.ok(member);
    }
}
