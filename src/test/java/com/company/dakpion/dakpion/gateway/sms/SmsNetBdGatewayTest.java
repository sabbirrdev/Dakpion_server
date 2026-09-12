package com.company.dakpion.dakpion.gateway.sms;

import com.company.dakpion.dakpion.config.DakpionProperties;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsBalanceReport;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsDeliveryReport;
import com.company.dakpion.dakpion.gateway.sms.dto.SmsSendResult;
import com.company.dakpion.dakpion.gateway.sms.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class SmsNetBdGatewayTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private DakpionProperties properties;
    private SmsNetBdGateway gateway;

    private static final String API_KEY = "test-secret-key-123";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
        properties = new DakpionProperties();
        properties.getSms().setApiKey(API_KEY);
        properties.getSms().setSenderId("DAKPION");

        gateway = new SmsNetBdGateway(properties, restTemplate, objectMapper);
    }

    @Test
    @DisplayName("Should successfully send SMS and parse requestId")
    void shouldSendSmsSuccessfully() {
        String jsonResponse = "{\"error\":0,\"msg\":\"SMS Sent successfully\",\"data\":{\"request_id\":\"REQ98765\"}}";

        mockServer.expect(requestTo("https://api.sms.net.bd/sendsms"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SmsSendResult result = gateway.send("01712345678", "Hello DakPion");

        assertTrue(result.isSuccess());
        assertEquals("REQ98765", result.getRequestId());
        mockServer.verify();
    }

    @Test
    @DisplayName("Should normalize various Bangladeshi phone number formats")
    void shouldNormalizeBangladeshiPhoneNumbers() {
        assertEquals("8801712345678", SmsNetBdGateway.normalizePhone("01712345678"));
        assertEquals("8801712345678", SmsNetBdGateway.normalizePhone("8801712345678"));
        assertEquals("8801712345678", SmsNetBdGateway.normalizePhone("+8801712345678"));
        assertEquals("8801912345678", SmsNetBdGateway.normalizePhone("01912345678"));

        assertThrows(SmsInvalidNumberException.class, () -> SmsNetBdGateway.normalizePhone("12345"));
        assertThrows(SmsInvalidNumberException.class, () -> SmsNetBdGateway.normalizePhone("01234567890"));
    }

    @Test
    @DisplayName("Should map error code 400-409 to SmsAuthException")
    void shouldMapAuthExceptions() {
        testErrorCode(400, SmsAuthException.class);
        testErrorCode(401, SmsAuthException.class);
        testErrorCode(402, SmsAuthException.class);
        testErrorCode(403, SmsAuthException.class);
        testErrorCode(404, SmsAuthException.class);
        testErrorCode(405, SmsAuthException.class);
        testErrorCode(409, SmsAuthException.class);
    }

    @Test
    @DisplayName("Should map error code 410-411 to SmsAccountExpiredException")
    void shouldMapAccountExpiredExceptions() {
        testErrorCode(410, SmsAccountExpiredException.class);
        testErrorCode(411, SmsAccountExpiredException.class);
    }

    @Test
    @DisplayName("Should map error code 412-415 to SmsInvalidPayloadException")
    void shouldMapPayloadExceptions() {
        testErrorCode(412, SmsInvalidPayloadException.class);
        testErrorCode(413, SmsInvalidPayloadException.class);
        testErrorCode(414, SmsInvalidPayloadException.class);
        testErrorCode(415, SmsInvalidPayloadException.class);
    }

    @Test
    @DisplayName("Should map error code 416 to SmsInvalidNumberException")
    void shouldMapInvalidNumberException() {
        testErrorCode(416, SmsInvalidNumberException.class);
    }

    @Test
    @DisplayName("Should map error code 417 to SmsInsufficientBalanceException")
    void shouldMapInsufficientBalanceException() {
        testErrorCode(417, SmsInsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should map error code 420 to SmsContentBlockedException")
    void shouldMapContentBlockedException() {
        testErrorCode(420, SmsContentBlockedException.class);
    }

    @Test
    @DisplayName("Should map error code 421 to SmsRestrictedNumberException")
    void shouldMapRestrictedNumberException() {
        testErrorCode(421, SmsRestrictedNumberException.class);
    }

    @Test
    @DisplayName("Should fetch and parse delivery report")
    void shouldParseDeliveryReport() {
        String jsonResponse = "{\"error\":0,\"msg\":\"Report found\",\"data\":{\"request_id\":\"REQ98765\",\"request_status\":\"Completed\",\"recipients\":[{\"number\":\"8801712345678\",\"charge\":\"0.35\",\"status\":\"Delivered\"}]}}";

        mockServer.expect(requestTo("https://api.sms.net.bd/report/request/REQ98765/?api_key=" + API_KEY))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SmsDeliveryReport report = gateway.getDeliveryReport("REQ98765");

        assertNotNull(report);
        assertEquals("REQ98765", report.getRequestId());
        assertEquals("Completed", report.getRequestStatus());
        assertEquals(1, report.getRecipients().size());
        assertEquals(new BigDecimal("0.35"), report.getRecipients().get(0).getCharge());
        assertEquals("Delivered", report.getRecipients().get(0).getStatus());
        mockServer.verify();
    }

    @Test
    @DisplayName("Should fetch and parse account balance")
    void shouldParseAccountBalance() {
        String jsonResponse = "{\"error\":0,\"msg\":\"Balance inquiry\",\"data\":{\"balance\":\"245.50\"}}";

        mockServer.expect(requestTo("https://api.sms.net.bd/user/balance/?api_key=" + API_KEY))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SmsBalanceReport balance = gateway.getBalance();

        assertNotNull(balance);
        assertEquals(new BigDecimal("245.50"), balance.getBalance());
        mockServer.verify();
    }

    @Test
    @DisplayName("Should never expose API key in masked phone strings")
    void shouldMaskPhoneProperly() {
        String masked = SmsNetBdGateway.maskPhone("8801712345678");
        assertEquals("8801****678", masked);
        assertFalse(masked.contains("1234"));
    }

    private void testErrorCode(int errorCode, Class<? extends SmsGatewayException> expectedExceptionClass) {
        mockServer.reset();
        String jsonResponse = "{\"error\":" + errorCode + ",\"msg\":\"Error description for " + errorCode + "\"}";

        mockServer.expect(requestTo("https://api.sms.net.bd/sendsms"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        assertThrows(expectedExceptionClass, () -> gateway.send("01712345678", "Test msg"));
        mockServer.verify();
    }
}
