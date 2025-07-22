package com.example.Throws.dto.auth.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
}
