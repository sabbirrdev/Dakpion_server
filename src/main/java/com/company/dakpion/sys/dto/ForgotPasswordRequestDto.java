package com.company.dakpion.sys.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequestDto {

    /** Phone number (01XXXXXXXXX) or username */
    @NotBlank(message = "Phone number or username is required")
    private String phoneOrUsername;
}
