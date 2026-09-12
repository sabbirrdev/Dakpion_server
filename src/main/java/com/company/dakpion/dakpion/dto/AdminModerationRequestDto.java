package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.ModerationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminModerationRequestDto {

    @NotNull(message = "Decision (APPROVED or REJECTED) is required")
    private ModerationStatus decision;

    private String reason;
}
