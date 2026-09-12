package com.company.dakpion.dakpion.controller;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.dto.DeliveryOptionDto;
import com.company.dakpion.dakpion.dto.LocalizedTextDto;
import com.company.dakpion.dakpion.dto.ThemeDefinitionDto;
import com.company.dakpion.dakpion.dto.ThemePaletteDto;
import com.company.dakpion.dakpion.service.DakpionCatalogService;
import com.company.dakpion.security.JWTEntryPoint;
import com.company.dakpion.security.JwtAuthFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DakpionCatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
class DakpionCatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DakpionCatalogService catalogService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JWTEntryPoint jwtEntryPoint;

    @Test
    @DisplayName("GET /api/v1/themes should return list of theme definitions wrapped in ServiceResult")
    void shouldReturnThemes() throws Exception {
        ThemeDefinitionDto theme = ThemeDefinitionDto.builder()
                .id("plain_digital")
                .name(LocalizedTextDto.builder().en("Plain Envelope").bn("সাধারণ খাম").build())
                .description(LocalizedTextDto.builder().en("Minimal").bn("সাদামাটা").build())
                .tier("FREE")
                .price(0.0)
                .palette(ThemePaletteDto.builder().paperBg("#F2E9D8").ink("#2B2118").build())
                .previewImage("plain")
                .build();

        when(catalogService.getThemes()).thenReturn(List.of(theme));

        mockMvc.perform(get("/api/v1/themes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("plain_digital"))
                .andExpect(jsonPath("$.data[0].name.en").value("Plain Envelope"))
                .andExpect(jsonPath("$.data[0].name.bn").value("সাধারণ খাম"))
                .andExpect(jsonPath("$.data[0].palette.paperBg").value("#F2E9D8"));
    }

    @Test
    @DisplayName("GET /api/v1/delivery-options should return options matching frontend contract")
    void shouldReturnDeliveryOptions() throws Exception {
        DeliveryOptionDto option = DeliveryOptionDto.builder()
                .type(DeliveryType.DIGITAL)
                .name(LocalizedTextDto.builder().en("Digital Link").bn("ডিজিটাল লিংক").build())
                .description(LocalizedTextDto.builder().en("Unique secret link").bn("ইউনিক লিংক").build())
                .price(0.0)
                .etaLabel(LocalizedTextDto.builder().en("Instant").bn("সাথে সাথে").build())
                .icon("link")
                .build();

        when(catalogService.getDeliveryOptions()).thenReturn(List.of(option));

        mockMvc.perform(get("/api/v1/delivery-options").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].type").value("DIGITAL"))
                .andExpect(jsonPath("$.data[0].icon").value("link"));
    }
}
