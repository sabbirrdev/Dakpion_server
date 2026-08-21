package com.company.efood.support.repository;

import com.company.efood.support.entity.SupportConversation;
import com.company.efood.support.entity.SupportConversationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportConversationMessageRepo extends JpaRepository<SupportConversationMessage, Long> {
    List<SupportConversationMessage> findByConversationOrderByEntryDateAsc(SupportConversation conversation);
}
