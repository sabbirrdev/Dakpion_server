package com.company.dakpion.dakpion.gateway.pdf;

import com.company.dakpion.dakpion.constant.DeliveryType;
import com.company.dakpion.dakpion.constant.LetterStatus;
import com.company.dakpion.dakpion.constant.LocaleCode;
import com.company.dakpion.dakpion.constant.ModerationStatus;
import com.company.dakpion.dakpion.constant.PaymentStatus;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PdfGenerationServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PdfGenerationService pdfGenerationService = new PdfGenerationService(objectMapper);

    @Test
    @DisplayName("Should generate valid vintage letter PDF byte stream")
    void shouldGenerateVintageLetterPdf() {
        DakpionLetterEntity letter = DakpionLetterEntity.builder()
                .id(UUID.randomUUID())
                .senderNickname("অচেনা পথিক")
                .senderPhoneHashed("f3a1...9c2e")
                .recipientName("তনিমা")
                .content("তনিমা,\nঅনেকদিন কথা হয় না। আজ হঠাৎ পুরনো গানটা শুনে তোমার কথা খুব মনে পড়ল। ভালো থেকো, খুব ভালো থেকো।\n— অচেনা পথিক")
                .themeId("vintage_premium_04")
                .audioId("gramophone_lofi_tune")
                .deliveryType(DeliveryType.PHYSICAL)
                .shippingAddress("{\"fullAddress\":\"House 12, Road 5, Dhanmondi\",\"city\":\"Dhaka\",\"postalCode\":\"1205\",\"phone\":\"01712345678\"}")
                .paymentStatus(PaymentStatus.PAID)
                .moderationStatus(ModerationStatus.APPROVED)
                .status(LetterStatus.DELIVERED)
                .language(LocaleCode.bn)
                .createdAt(LocalDateTime.now())
                .build();

        byte[] pdfBytes = pdfGenerationService.generateVintageLetterPdf(letter);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF should not be empty");

        // PDF magic header %PDF-
        String header = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", header);
    }
}
