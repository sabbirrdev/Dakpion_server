package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.LocaleCode;
import com.company.dakpion.dakpion.constant.PaymentStatus;
import com.company.dakpion.dakpion.dto.ComposeLetterRequestDto;
import com.company.dakpion.dakpion.dto.LetterResponseDto;
import com.company.dakpion.dakpion.service.DakpionLetterService;
import com.company.dakpion.security.JWTEntryPoint;
import com.company.dakpion.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DakpionLetterController.class)
@AutoConfigureMockMvc(addFilters = false)
class DakpionLetterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DakpionLetterService letterService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JWTEntryPoint jwtEntryPoint;

    @Test
    @DisplayName("POST /api/v1/letters/compose should return 201 with composed letter")
    void shouldComposeLetter() throws Exception {
        ComposeLetterRequestDto request = ComposeLetterRequestDto.builder()
                .senderNickname("অচেনা পথিক")
                .senderPhone("01712345678")
                .recipientName("তনিমা")
                .content("অনেকদিন কথা হয় না।")
                .themeId("vintage_premium_04")
                .audioId("rain_window")
                .deliveryType(DeliveryType.DIGITAL)
                .language(LocaleCode.bn)
                .otpVerificationToken("valid-token")
                .build();

        LetterResponseDto responseDto = LetterResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .senderNickname("অচেনা পথিক")
                .senderPhoneHashed("f3a1...9c2e")
                .recipientName("তনিমা")
                .content("অনেকদিন কথা হয় না।")
                .themeId("vintage_premium_04")
                .audioId("rain_window")
                .deliveryType(DeliveryType.DIGITAL)
                .paymentStatus(PaymentStatus.NOT_APPLICABLE)
                .status(LetterStatus.SUBMITTED)
                .language(LocaleCode.bn)
                .build();

        when(letterService.composeLetter(any(ComposeLetterRequestDto.class), eq("idemp-123")))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/letters/compose")
                        .header("Idempotency-Key", "idemp-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.senderNickname").value("অচেনা পথিক"))
                .andExpect(jsonPath("$.data.senderPhoneHashed").value("f3a1...9c2e"))
                .andExpect(jsonPath("$.data.content").value("অনেকদিন কথা হয় না।"));
    }

    @Test
    @DisplayName("GET /api/v1/letters/{id} should return public letter details")
    void shouldGetLetterById() throws Exception {
        UUID letterId = UUID.randomUUID();
        LetterResponseDto responseDto = LetterResponseDto.builder()
                .id(letterId.toString())
                .senderNickname("অচেনা পথিক")
                .recipientName("তনিমা")
                .content("হ্যালো")
                .themeId("vintage_premium_04")
                .audioId("rain_window")
                .deliveryType(DeliveryType.DIGITAL)
                .status(LetterStatus.DELIVERED)
                .language(LocaleCode.bn)
                .build();

        when(letterService.getLetterById(letterId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/letters/" + letterId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(letterId.toString()))
                .andExpect(jsonPath("$.data.senderNickname").value("অচেনা পথিক"));
    }

    @Test
    @DisplayName("POST /api/v1/letters/{id}/open should mark letter opened")
    void shouldMarkLetterOpened() throws Exception {
        UUID letterId = UUID.randomUUID();
        LetterResponseDto responseDto = LetterResponseDto.builder()
                .id(letterId.toString())
                .status(LetterStatus.OPENED)
                .openedAt("2026-09-05T10:00:00")
                .build();

        when(letterService.markOpened(letterId)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/letters/" + letterId + "/open").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("OPENED"));
    }
}
