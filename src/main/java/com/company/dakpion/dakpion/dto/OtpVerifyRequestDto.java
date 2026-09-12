package com.company.dakpion.dakpion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequestDto {

    @NotBlank(message = "requestId is required")
    private String requestId;

    @NotBlank(message = "OTP code is required")
    private String code;
}
