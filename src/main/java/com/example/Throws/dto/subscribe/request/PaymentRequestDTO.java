package com.example.Throws.dto.subscribe.request;

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
    private Boolean autoRenewal;
}
