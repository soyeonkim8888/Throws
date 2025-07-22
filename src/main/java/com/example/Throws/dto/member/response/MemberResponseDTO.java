package com.example.Throws.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.Throws.domain.Role;

@Getter
@AllArgsConstructor
@Builder
public class MemberResponseDTO {
    private final Long id;
    private final String email;
    private final Role role;
    private final String providerName;
    private final Long providerId;

}
