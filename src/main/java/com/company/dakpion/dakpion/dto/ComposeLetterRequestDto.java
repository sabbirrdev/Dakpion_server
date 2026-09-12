package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LocaleCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComposeLetterRequestDto {

    @NotBlank(message = "Sender nickname is required")
    @Size(max = 100, message = "Sender nickname cannot exceed 100 characters")
    private String senderNickname;

    @NotBlank(message = "Sender phone is required")
    @Size(max = 20, message = "Invalid phone number format")
    private String senderPhone;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Recipient name cannot exceed 100 characters")
    private String recipientName;

    @Size(max = 20, message = "Invalid recipient phone number")
    private String recipientPhone;

    @NotBlank(message = "Letter content is required")
    @Size(max = 10000, message = "Letter content cannot exceed 10,000 characters")
    private String content;

    @NotBlank(message = "Theme ID is required")
    private String themeId;

    @NotBlank(message = "Audio ID is required")
    private String audioId;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;

    private ShippingAddressDto shippingAddress;

    @NotNull(message = "Language code is required")
    private LocaleCode language;

    private String otpRequestId;
    private String otpVerificationToken;
}
