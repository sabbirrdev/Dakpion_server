package com.company.efood.support.controller;

import com.company.efood.base.BaseResponse;
import com.company.efood.base.BaseUtils;
import com.company.efood.support.entity.SupportConversation;
import com.company.efood.support.entity.SupportConversationMessage;
import com.company.efood.support.repository.SupportConversationMessageRepo;
import com.company.efood.support.repository.SupportConversationRepo;
import com.company.efood.sys.entity.AppUser;
import com.company.efood.sys.repository.AppUserRepo;
import com.company.efood.sys.utils.AuthTokenUtils;
import com.company.efood.websocket.service.LiveNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.company.efood.base.BaseConstants.CUSTOMER_END_POINT;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE;
import static com.company.efood.base.BaseConstants.PROCESS_COMPLETE_BN;

@RestController
@RequestMapping(CUSTOMER_END_POINT + "support")
@AllArgsConstructor
public class SupportChatController {

    private final SupportConversationRepo supportConversationRepo;
    private final SupportConversationMessageRepo supportConversationMessageRepo;
    private final AppUserRepo appUserRepo;
    private final BaseUtils baseUtils;
    private final AuthTokenUtils authTokenUtils;
    private final LiveNotificationService liveNotificationService;

    @PostMapping("/chat")
    public BaseResponse createConversation(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            String subject = body.getOrDefault("subject", "Customer support");
            String message = body.getOrDefault("message", "");
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUser customer = appUserRepo.findById(userId).orElseThrow(() -> new RuntimeException("Customer not found"));

            SupportConversation conversation = new SupportConversation();
            conversation.setCustomer(customer);
            conversation.setSubject(subject);
            conversation.setStatus("OPEN");
            conversation.setLastMessage(message);
            conversation.setEntryUser(userId);
            conversation.setEntryDate(LocalDateTime.now());
            SupportConversation savedConversation = supportConversationRepo.save(conversation);

            if (!message.isBlank()) {
                SupportConversationMessage firstMessage = new SupportConversationMessage();
                firstMessage.setConversation(savedConversation);
                firstMessage.setSenderRole("CUSTOMER");
                firstMessage.setMessageText(message);
                firstMessage.setEntryUser(userId);
                firstMessage.setEntryDate(LocalDateTime.now());
                supportConversationMessageRepo.save(firstMessage);

                liveNotificationService.sendSupportMessage(savedConversation.getId(), Map.of(
                        "conversationId", savedConversation.getId(),
                        "senderRole", "CUSTOMER",
                        "message", message
                ));
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("conversationId", savedConversation.getId());
            payload.put("subject", savedConversation.getSubject());
            payload.put("status", savedConversation.getStatus());
            return baseUtils.generateSuccessResponse(payload, "Support ticket created", "সাপোর্ট টিকিট তৈরি হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/chat")
    public BaseResponse getMyConversations(HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUser customer = appUserRepo.findById(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
            List<SupportConversation> conversations = supportConversationRepo.findByCustomerOrderByEntryDateDesc(customer);
            return baseUtils.generateSuccessResponse(conversations, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @GetMapping("/chat/{conversationId}")
    public BaseResponse getConversation(@PathVariable Long conversationId, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            AppUser customer = appUserRepo.findById(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
            SupportConversation conversation = supportConversationRepo.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            if (!conversation.getCustomer().getId().equals(customer.getId())) {
                throw new RuntimeException("Conversation does not belong to the current customer");
            }

            List<SupportConversationMessage> messages = supportConversationMessageRepo.findByConversationOrderByEntryDateAsc(conversation);
            Map<String, Object> payload = new HashMap<>();
            payload.put("conversation", conversation);
            payload.put("messages", messages);
            return baseUtils.generateSuccessResponse(payload, PROCESS_COMPLETE, PROCESS_COMPLETE_BN);
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }

    @PostMapping("/chat/{conversationId}/message")
    public BaseResponse sendMessage(@PathVariable Long conversationId, @RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            Long userId = authTokenUtils.getUserIdFromRequest(request);
            String messageText = body.get("message");
            if (messageText == null || messageText.isBlank()) {
                return BaseResponse.builder()
                        .status(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Message cannot be empty")
                        .build();
            }

            AppUser customer = appUserRepo.findById(userId).orElseThrow(() -> new RuntimeException("Customer not found"));
            SupportConversation conversation = supportConversationRepo.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            if (!conversation.getCustomer().getId().equals(customer.getId())) {
                throw new RuntimeException("Conversation does not belong to the current customer");
            }

            SupportConversationMessage message = new SupportConversationMessage();
            message.setConversation(conversation);
            message.setSenderRole("CUSTOMER");
            message.setMessageText(messageText);
            message.setEntryUser(userId);
            message.setEntryDate(LocalDateTime.now());
            supportConversationMessageRepo.save(message);

            conversation.setLastMessage(messageText);
            conversation.setStatus("OPEN");
            supportConversationRepo.save(conversation);

            liveNotificationService.sendSupportMessage(conversationId, Map.of(
                    "conversationId", conversationId,
                    "senderRole", "CUSTOMER",
                    "message", messageText
            ));

            return baseUtils.generateSuccessResponse(message, "Message sent", "মেসেজ পাঠানো হয়েছে");
        } catch (Exception ex) {
            return baseUtils.generateErrorResponse(ex);
        }
    }
}
