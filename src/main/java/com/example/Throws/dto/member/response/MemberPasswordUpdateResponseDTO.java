package com.example.Throws.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberPasswordUpdateResponseDTO {
    private boolean success;         // true/false
    private String  message;
}
