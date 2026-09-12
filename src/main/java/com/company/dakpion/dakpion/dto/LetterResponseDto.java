package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.LocaleCode;
import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LetterResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String senderNickname;
    private String senderPhoneHashed;
    private String recipientName;
    private String recipientPhone;
    private String content;
    private String themeId;
    private String audioId;
    private DeliveryType deliveryType;
    private ShippingAddressDto shippingAddress;
    private PaymentStatus paymentStatus;
    private ModerationStatus moderationStatus;
    private LetterStatus status;
    private String createdAt;
    private String openedAt;
    private LocaleCode language;
    private String courierBookingId;
    private String courierTrackingId;
    private String courierStatus;
}
