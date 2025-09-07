package com.nuitee.domain.policy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class I18nFallbackPolicyTest {
    private final I18nFallbackPolicy policy = new I18nFallbackPolicy();

    @Test
    void returnsDefaultWhenRequestLangIsNullOrBlank() {
        assertEquals(List.of("fr"), policy.getFallbackChain(null));
        assertEquals(List.of("fr"), policy.getFallbackChain(""));
    }

    @Test
    void returnsDefaultWhenRequestLangIsDefault() {
        assertEquals(List.of("fr"), policy.getFallbackChain("fr"));
        assertEquals(List.of("fr"), policy.getFallbackChain("FR"));
    }

    @Test
    void returnsRequestedLangThenDefault() {
        assertEquals(List.of("en", "fr"), policy.getFallbackChain("en"));
    }
}
