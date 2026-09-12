package com.company.dakpion.dakpion.dto;

import com.company.dakpion.dakpion.constant.DeliveryEventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddDeliveryEventRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private DeliveryEventStatus status;
    private String note;
    private LocalDateTime occurredAt;
}
