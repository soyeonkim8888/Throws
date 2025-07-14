package com.example.Throws.DTO.Subscribe;

import com.example.Throws.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Long memberId;
    private String paymentProvider;
    private String paymentId;
    private int price;
}
