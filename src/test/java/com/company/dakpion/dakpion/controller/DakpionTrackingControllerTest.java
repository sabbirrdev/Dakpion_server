package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.constant.DeliveryEventStatus;
import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.dto.PublicTrackingDto;
import com.company.dakpion.dakpion.entity.DakpionDeliveryEventEntity;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.company.dakpion.dakpion.exception.ResourceNotFoundException;
import com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo;
import com.company.dakpion.dakpion.repository.DakpionLetterRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DakpionTrackingControllerTest {

    @Mock
    private DakpionLetterRepo letterRepo;
    @Mock
    private DakpionDeliveryEventRepo deliveryEventRepo;
    @Mock
    private HttpServletRequest httpRequest;

    private DakpionTrackingController trackingController;

    @BeforeEach
    void setUp() {
        trackingController = new DakpionTrackingController(letterRepo, deliveryEventRepo);
    }

    @Test
    @DisplayName("Should return tracking details with zero PII for physical letter")
    void shouldReturnTrackingDetailsWithZeroPii() {
        String trackingCode = "DP-7F3K9Q2R";
        UUID letterId = UUID.randomUUID();

        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        DakpionLetterEntity letter = DakpionLetterEntity.builder()
                .id(letterId)
                .trackingCode(trackingCode)
                .deliveryType(DeliveryType.PHYSICAL)
                .senderNickname("Secret Admirer")
                .recipientName("Sadia")
                .recipientPhone("01712345678")
                .content("Super secret love letter")
                .status(LetterStatus.SUBMITTED)
                .build();

        DakpionDeliveryEventEntity event1 = DakpionDeliveryEventEntity.builder()
                .letterId(letterId)
                .status(DeliveryEventStatus.SUBMITTED)
                .note("Letter submitted by sender")
                .occurredAt(LocalDateTime.now().minusHours(2))
                .createdBy("SYSTEM")
                .build();

        DakpionDeliveryEventEntity event2 = DakpionDeliveryEventEntity.builder()
                .letterId(letterId)
                .status(DeliveryEventStatus.PRINTING)
                .note("Letter printed on parchment")
                .occurredAt(LocalDateTime.now().minusHours(1))
                .createdBy("ADMIN:1")
                .build();

        when(letterRepo.findByTrackingCode(trackingCode)).thenReturn(Optional.of(letter));
        when(deliveryEventRepo.findByLetterIdOrderByOccurredAtAsc(letterId)).thenReturn(List.of(event1, event2));

        ResponseEntity<PublicTrackingDto> response = trackingController.track(trackingCode, httpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        PublicTrackingDto body = response.getBody();
        assertEquals(trackingCode, body.getTrackingCode());
        assertEquals("PRINTING", body.getCurrentStatus());
        assertEquals(2, body.getEvents().size());
        assertEquals("SUBMITTED", body.getEvents().get(0).getStatus());
        assertEquals("PRINTING", body.getEvents().get(1).getStatus());

        // STRICT PII CHECK: Ensure no sensitive field exists on the DTO class
        assertFalse(body.toString().contains("Sadia"), "Response must not contain recipient name");
        assertFalse(body.toString().contains("Secret Admirer"), "Response must not contain sender nickname");
        assertFalse(body.toString().contains("01712345678"), "Response must not contain recipient phone");
        assertFalse(body.toString().contains("Super secret love letter"), "Response must not contain letter content");
    }

    @Test
    @DisplayName("Should return 404 for non-physical letters or invalid codes")
    void shouldReturn404ForNonPhysicalLetter() {
        String trackingCode = "DP-DIGITAL";
        UUID letterId = UUID.randomUUID();

        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        // Digital letter should not be returned by tracking
        DakpionLetterEntity digitalLetter = DakpionLetterEntity.builder()
                .id(letterId)
                .trackingCode(trackingCode)
                .deliveryType(DeliveryType.DIGITAL)
                .build();

        when(letterRepo.findByTrackingCode(trackingCode)).thenReturn(Optional.of(digitalLetter));

        assertThrows(ResourceNotFoundException.class, () -> trackingController.track(trackingCode, httpRequest));
    }
}
