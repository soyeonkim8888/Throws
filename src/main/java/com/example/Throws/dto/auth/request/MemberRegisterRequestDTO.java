package com.example.Throws.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
public class MemberRegisterRequestDTO {
    @NotBlank  @Email
    private String email;

    @NotBlank
    private String password;

    private String providerName;
    private String providerId;
}
