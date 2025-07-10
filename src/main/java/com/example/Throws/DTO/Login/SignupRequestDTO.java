package com.example.Throws.DTO.Login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    @NotBlank(message = "이메일은 입력해주세요")
    @Email(message = "sefdksfj@email.com")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주쇼")
    private String password;
}
