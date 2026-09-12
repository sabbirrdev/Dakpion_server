package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.dto.AdminCourierBookingRequestDto;
import com.company.dakpion.dakpion.dto.AdminCourierBookingResponseDto;
import com.company.dakpion.dakpion.dto.AdminModerationRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.service.DakpionAdminService;
import com.company.dakpion.security.JWTEntryPoint;
import com.company.dakpion.security.JwtAuthFilter;
import com.company.dakpion.sys.utils.AuthTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DakpionAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class DakpionAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DakpionAdminService adminService;

    @MockBean
    private AuthTokenUtils authTokenUtils;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JWTEntryPoint jwtEntryPoint;

    @MockBean
    private com.company.dakpion.dakpion.repository.DakpionDeliveryEventRepo deliveryEventRepo;

    @MockBean
    private com.company.dakpion.dakpion.repository.DakpionLetterRepo letterRepo;

    @Test
    @DisplayName("GET /api/v1/admin/letters should return moderation queue")
    void shouldGetModerationQueue() throws Exception {
        LetterResponseDto letter = LetterResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .senderNickname("অচেনা")
                .recipientName("বন্ধু")
                .moderationStatus(ModerationStatus.PENDING)
                .build();

        when(adminService.getModerationQueue(eq(ModerationStatus.PENDING), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(letter)));

        mockMvc.perform(get("/api/v1/admin/letters?status=PENDING").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].senderNickname").value("অচেনা"));
    }

    @Test
    @DisplayName("POST /api/v1/admin/letters/{id}/moderate should approve letter")
    void shouldModerateLetter() throws Exception {
        UUID id = UUID.randomUUID();
        AdminModerationRequestDto req = AdminModerationRequestDto.builder()
                .decision(ModerationStatus.APPROVED)
                .reason("Approved for delivery")
                .build();

        LetterResponseDto letter = LetterResponseDto.builder()
                .id(id.toString())
                .moderationStatus(ModerationStatus.APPROVED)
                .build();

        when(authTokenUtils.getUserIdFromRequest(any())).thenReturn(101L);
        when(adminService.moderateLetter(eq(id), any(AdminModerationRequestDto.class), eq(101L)))
                .thenReturn(letter);

        mockMvc.perform(post("/api/v1/admin/letters/" + id + "/moderate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.moderationStatus").value("APPROVED"));
    }

    @Test
    @DisplayName("POST /api/v1/admin/letters/{id}/courier-book should book courier shipment")
    void shouldBookCourier() throws Exception {
        UUID id = UUID.randomUUID();
        AdminCourierBookingRequestDto req = AdminCourierBookingRequestDto.builder()
                .courierProvider("PATHAO")
                .build();

        AdminCourierBookingResponseDto booking = AdminCourierBookingResponseDto.builder()
                .letterId(id.toString())
                .courierProvider("PATHAO")
                .bookingId("PTH-12345")
                .trackingCode("PTH-TRK-98765")
                .status("ACCEPTED")
                .build();

        when(adminService.bookCourier(eq(id), any(AdminCourierBookingRequestDto.class)))
                .thenReturn(booking);

        mockMvc.perform(post("/api/v1/admin/letters/" + id + "/courier-book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trackingCode").value("PTH-TRK-98765"));
    }

    @Test
    @DisplayName("GET /api/v1/admin/letters/{id}/print-pdf should return PDF content")
    void shouldPrintPdf() throws Exception {
        UUID id = UUID.randomUUID();
        byte[] pdfBytes = "%PDF-1.4 test bytes".getBytes();

        when(adminService.printVintagePdf(id)).thenReturn(pdfBytes);

        mockMvc.perform(get("/api/v1/admin/letters/" + id + "/print-pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(content().bytes(pdfBytes));
    }
}
