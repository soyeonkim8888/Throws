package com.example.Throws.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDeleteResponseDTO {
    private final boolean success;
    private final String  message;
}
