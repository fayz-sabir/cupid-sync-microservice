package com.nuitee.domain.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LangTest {

    @Test
    void fromCodeParsesKnownCodes() {
        assertEquals(Lang.FR, Lang.fromCode("fr"));
        assertEquals(Lang.ES, Lang.fromCode("ES"));
    }

    @Test
    void fromCodeDefaultsToEnForUnknownOrNull() {
        assertEquals(Lang.EN, Lang.fromCode("de"));
        assertEquals(Lang.EN, Lang.fromCode(null));
    }
}
