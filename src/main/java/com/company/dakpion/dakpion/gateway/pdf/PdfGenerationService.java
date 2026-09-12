package com.company.dakpion.dakpion.gateway.pdf;

import com.company.dakpion.dakpion.dto.ShippingAddressDto;
import com.company.dakpion.dakpion.entity.DakpionLetterEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final ObjectMapper objectMapper;

    public byte[] generateVintageLetterPdf(DakpionLetterEntity letter) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            // Add vintage parchment background event
            writer.setPageEvent(new VintageBackgroundPageEvent());

            document.open();

            // Load Kalpana UNICODE font for Bengali script rendering
            BaseFont kalpanaBase = getKalpanaBaseFont();

            Font headerFont = new Font(kalpanaBase, 18, Font.BOLD, new Color(43, 33, 24));
            Font subHeaderFont = new Font(kalpanaBase, 11, Font.ITALIC, new Color(122, 139, 111));
            Font metaFont = FontFactory.getFont(FontFactory.COURIER, 10, new Color(70, 60, 50));
            Font dearFont = new Font(kalpanaBase, 14, Font.BOLD, new Color(43, 33, 24));
            Font bodyFont = new Font(kalpanaBase, 13, Font.NORMAL, new Color(43, 33, 24));
            Font signoffFont = new Font(kalpanaBase, 12, Font.ITALIC, new Color(60, 50, 40));
            Font senderFont = new Font(kalpanaBase, 13, Font.BOLD, new Color(43, 33, 24));
            Font footerFont = new Font(kalpanaBase, 9, Font.NORMAL, new Color(100, 90, 80));

            // 1. Postmark & Stamp Header Table
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{65, 35});

            // Left side: Postmark Seal
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            Paragraph titlePara = new Paragraph("DAKPION POSTBOX (ডাকপিওন)", headerFont);
            Paragraph subPara = new Paragraph("Vintage Correspondence & Secret Dispatch", subHeaderFont);
            leftCell.addElement(titlePara);
            leftCell.addElement(subPara);
            headerTable.addCell(leftCell);

            // Right side: Postal Stamp & Date Box
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.BOX);
            rightCell.setBorderColor(new Color(166, 61, 64));
            rightCell.setBorderWidth(1.5f);
            rightCell.setPadding(8);
            rightCell.setBackgroundColor(new Color(254, 250, 240));

            String formattedDate = letter.getCreatedAt() != null 
                    ? letter.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) 
                    : "N/A";

            Paragraph stampHeader = new Paragraph("★ OFFICIAL DISPATCH ★", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, new Color(166, 61, 64)));
            Paragraph stampDate = new Paragraph("DISPATCHED: " + formattedDate, metaFont);
            Paragraph letterIdMeta = new Paragraph("ID: " + letter.getId().toString().substring(0, 8).toUpperCase(), metaFont);

            stampHeader.setAlignment(Element.ALIGN_CENTER);
            stampDate.setAlignment(Element.ALIGN_CENTER);
            letterIdMeta.setAlignment(Element.ALIGN_CENTER);

            rightCell.addElement(stampHeader);
            rightCell.addElement(stampDate);
            rightCell.addElement(letterIdMeta);
            headerTable.addCell(rightCell);

            document.add(headerTable);
            document.add(new Paragraph("\n"));

            // 2. Addressing Line
            Paragraph toPara = new Paragraph("Dear " + (letter.getRecipientName() != null ? letter.getRecipientName() : "") + ",", dearFont);
            toPara.setSpacingAfter(15);
            document.add(toPara);

            // 3. Body Content (Clean HTML to formatted text)
            String rawContent = letter.getContent() != null ? letter.getContent() : "";
            String cleanBody = rawContent
                    .replaceAll("(?i)<br\\s*/?>", "\n")
                    .replaceAll("(?i)</p>", "\n\n")
                    .replaceAll("<[^>]*>", "")
                    .replace("&nbsp;", " ")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .trim();

            Paragraph contentPara = new Paragraph(cleanBody, bodyFont);
            contentPara.setLeading(22f);
            contentPara.setSpacingAfter(25);
            document.add(contentPara);

            // 4. Sender Nickname / Signoff
            Paragraph signoff = new Paragraph("Yours truly,", signoffFont);
            Paragraph senderPara = new Paragraph("— " + (letter.getSenderNickname() != null ? letter.getSenderNickname() : ""), senderFont);
            signoff.setSpacingAfter(5);
            senderPara.setSpacingAfter(25);
            document.add(signoff);
            document.add(senderPara);

            // 5. Shipping & Courier Box (if physical delivery)
            if (letter.getShippingAddress() != null && !letter.getShippingAddress().isBlank()) {
                try {
                    ShippingAddressDto address = objectMapper.readValue(letter.getShippingAddress(), ShippingAddressDto.class);
                    PdfPTable addressTable = new PdfPTable(1);
                    addressTable.setWidthPercentage(100);

                    PdfPCell addressCell = new PdfPCell();
                    addressCell.setBorder(Rectangle.BOX);
                    addressCell.setBorderColor(new Color(122, 139, 111));
                    addressCell.setBackgroundColor(new Color(245, 238, 221));
                    addressCell.setPadding(10);

                    Paragraph addressHeader = new Paragraph("DESTINATION ADDRESS (PHYSICAL FULFILLMENT):", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(43, 33, 24)));
                    Paragraph addressBody = new Paragraph(
                            address.getFullAddress() + ", " + address.getCity() + 
                            (address.getPostalCode() != null ? " - " + address.getPostalCode() : "") + 
                            "\nRecipient Phone: " + address.getPhone(),
                            footerFont
                    );

                    addressCell.addElement(addressHeader);
                    addressCell.addElement(addressBody);
                    addressTable.addCell(addressCell);

                    document.add(addressTable);
                } catch (Exception e) {
                    log.warn("Failed to parse shipping address for letter {}", letter.getId());
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to generate PDF for letter ID: {}", letter.getId(), e);
            throw new RuntimeException("Failed to generate vintage letter PDF", e);
        }
    }

    /**
     * Loads Kalpana Unicode TTF font from resources with IDENTITY_H for Bengali rendering.
     */
    private BaseFont getKalpanaBaseFont() {
        try {
            try (java.io.InputStream is = getClass().getResourceAsStream("/fonts/KalpanaUnicode.ttf")) {
                if (is != null) {
                    byte[] fontBytes = is.readAllBytes();
                    return BaseFont.createFont("KalpanaUnicode.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
                }
            }
            try (java.io.InputStream is = getClass().getResourceAsStream("/fonts/Kalpana UNICODE.ttf")) {
                if (is != null) {
                    byte[] fontBytes = is.readAllBytes();
                    return BaseFont.createFont("Kalpana UNICODE.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontBytes, null);
                }
            }
        } catch (Exception e) {
            log.warn("Could not load Kalpana font for PDF generation, falling back to standard font: {}", e.getMessage());
        }
        try {
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize fallback font", e);
        }
    }

    /**
     * Renders vintage parchment tone background and ornate decorative border.
     */
    private static class VintageBackgroundPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContentUnder();
            Rectangle rect = document.getPageSize();

            // Parchment tone background (#F2E9D8)
            cb.saveState();
            cb.setColorFill(new Color(242, 233, 216));
            cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight());
            cb.fill();

            // Decorative outer border
            cb.setColorStroke(new Color(180, 160, 130));
            cb.setLineWidth(2f);
            cb.rectangle(rect.getLeft() + 20, rect.getBottom() + 20, rect.getWidth() - 40, rect.getHeight() - 40);
            cb.stroke();

            // Decorative inner border
            cb.setColorStroke(new Color(210, 195, 170));
            cb.setLineWidth(0.8f);
            cb.rectangle(rect.getLeft() + 25, rect.getBottom() + 25, rect.getWidth() - 50, rect.getHeight() - 50);
            cb.stroke();

            cb.restoreState();
        }
    }
}
