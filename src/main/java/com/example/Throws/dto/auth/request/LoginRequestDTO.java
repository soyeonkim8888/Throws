package com.example.Throws.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "이메일은 입력해주세요")
    @Email(message = "sefdksfj@email.com")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

}
