package com.nuitee.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Pattern;

public class LangValidator implements ConstraintValidator<ValidLang, String> {

    private static final Pattern ISO2 = Pattern.compile("^[a-z]{2}$", Pattern.CASE_INSENSITIVE);
    private static final Set<String> SUPPORTED = Set.of("en", "fr", "es");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return false;
        if (!ISO2.matcher(value).matches()) return false;
        return SUPPORTED.contains(value.toLowerCase());
    }
}
