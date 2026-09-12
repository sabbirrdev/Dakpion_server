package com.company.dakpion.dakpion.service;

import com.company.dakpion.dakpion.entity.DakpionBlockedWordEntity;
import com.company.dakpion.dakpion.exception.ContentRejectedException;
import com.company.dakpion.dakpion.repository.DakpionBlockedWordRepo;
import com.company.dakpion.dakpion.service.impl.BadWordsFilterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BadWordsFilterServiceTest {

    @Mock
    private DakpionBlockedWordRepo blockedWordRepo;

    private BadWordsFilterServiceImpl filterService;

    @BeforeEach
    void setUp() {
        when(blockedWordRepo.findAllByActiveTrue()).thenReturn(List.of(
                DakpionBlockedWordEntity.builder().term("idiot").build(),
                DakpionBlockedWordEntity.builder().term("stupid").build(),
                DakpionBlockedWordEntity.builder().term("বোকা").build()
        ));
        filterService = new BadWordsFilterServiceImpl(blockedWordRepo);
        filterService.init();
    }

    @Test
    @DisplayName("Should detect blocked English abusive terms")
    void shouldDetectEnglishBlockedWords() {
        assertTrue(filterService.containsBlockedLanguage("You are an idiot!"));
        assertTrue(filterService.containsBlockedLanguage("This is STUPID"));
        assertThrows(ContentRejectedException.class, () -> filterService.validateContent("You idiot"));
    }

    @Test
    @DisplayName("Should detect blocked Bengali abusive terms")
    void shouldDetectBengaliBlockedWords() {
        assertTrue(filterService.containsBlockedLanguage("তুমি একটা বোকা মানুষ"));
        assertThrows(ContentRejectedException.class, () -> filterService.validateContent("একদম বোকা!"));
    }

    @Test
    @DisplayName("Should pass wholesome, clean nostalgic letter content")
    void shouldPassCleanContent() {
        String wholesomeLetter = "তনিমা,\nঅনেকদিন কথা হয় না। আজ হঠাৎ পুরনো গানটা শুনে তোমার কথা খুব মনে পড়ল। ভালো থেকো।";
        assertFalse(filterService.containsBlockedLanguage(wholesomeLetter));
        assertDoesNotThrow(() -> filterService.validateContent(wholesomeLetter));

        String englishWholesome = "Hey Arif, just wanted to say thank you for everything. See you Friday!";
        assertFalse(filterService.containsBlockedLanguage(englishWholesome));
        assertDoesNotThrow(() -> filterService.validateContent(englishWholesome));
    }
}
