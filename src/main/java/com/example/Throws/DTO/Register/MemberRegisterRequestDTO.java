package com.example.Throws.DTO.Register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterRequestDTO {
    private String email;
    private String password;
    private String providerName;
    private String providerId;
}
