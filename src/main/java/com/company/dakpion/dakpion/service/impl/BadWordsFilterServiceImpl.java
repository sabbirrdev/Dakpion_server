package com.company.dakpion.dakpion.service.impl;

import com.company.dakpion.dakpion.entity.DakpionBlockedWordEntity;
import com.company.dakpion.dakpion.exception.ContentRejectedException;
import com.company.dakpion.dakpion.repository.DakpionBlockedWordRepo;
import com.company.dakpion.dakpion.service.BadWordsFilterService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadWordsFilterServiceImpl implements BadWordsFilterService {

    private final DakpionBlockedWordRepo blockedWordRepo;
    private final Set<String> cachedTerms = new HashSet<>();

    private static final List<String> DEFAULT_FALLBACK_TERMS = List.of(
            "idiot", "stupid", "hate you", "kill you", "বোকা", "মূর্খ", "ঘৃণা করি"
    );

    @PostConstruct
    public void init() {
        refreshBlocklist();
    }

    @Scheduled(fixedRate = 600000) // Refresh cache every 10 minutes
    public void refreshBlocklist() {
        try {
            List<DakpionBlockedWordEntity> entities = blockedWordRepo.findAllByActiveTrue();
            synchronized (cachedTerms) {
                cachedTerms.clear();
                cachedTerms.addAll(DEFAULT_FALLBACK_TERMS);
                for (DakpionBlockedWordEntity entity : entities) {
                    if (entity.getTerm() != null && !entity.getTerm().isBlank()) {
                        cachedTerms.add(entity.getTerm().trim().toLowerCase());
                    }
                }
            }
            log.info("[BadWordsFilter] Loaded {} blocked terms into moderation cache.", cachedTerms.size());
        } catch (Exception e) {
            log.warn("[BadWordsFilter] Failed to load blocked terms from database, using defaults: {}", e.getMessage());
            synchronized (cachedTerms) {
                cachedTerms.addAll(DEFAULT_FALLBACK_TERMS);
            }
        }
    }

    @Override
    public boolean containsBlockedLanguage(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase();
        synchronized (cachedTerms) {
            for (String term : cachedTerms) {
                if (normalized.contains(term.toLowerCase())) {
                    log.warn("[BadWordsFilter] Content violation detected for term: {}", term);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void validateContent(String text) {
        if (containsBlockedLanguage(text)) {
            throw new ContentRejectedException("Your letter contains language that isn't allowed. Please revise and try again.");
        }
    }
}
