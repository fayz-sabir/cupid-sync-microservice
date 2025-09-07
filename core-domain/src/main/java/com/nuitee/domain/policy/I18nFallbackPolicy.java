package com.nuitee.domain.policy;

import java.util.List;

public class I18nFallbackPolicy {
    private static final String DEFAULT_LANG = "fr";

    public List<String> getFallbackChain(String requestLang) {
        if (requestLang == null || requestLang.isBlank()) {
            return List.of(DEFAULT_LANG);
        }
        if (requestLang.equalsIgnoreCase(DEFAULT_LANG)) {
            return List.of(DEFAULT_LANG);
        }
        return List.of(requestLang, DEFAULT_LANG);
    }
}
