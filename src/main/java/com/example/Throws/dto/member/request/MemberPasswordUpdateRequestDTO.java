package com.example.Throws.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberPasswordUpdateRequestDTO {
    @NotBlank(message = "현재 비밀번호를 입력해 주세요")
    private String currentPassword;   // 선택: 현재 PW 확인용

    @NotBlank(message = "새 비밀번호를 입력해 주세요")
    private String newPassword;
}
