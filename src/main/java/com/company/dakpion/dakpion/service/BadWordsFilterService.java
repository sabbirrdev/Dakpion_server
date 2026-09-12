package com.company.dakpion.dakpion.service;

public interface BadWordsFilterService {
    boolean containsBlockedLanguage(String text);
    void validateContent(String text);
}
