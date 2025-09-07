package com.nuitee.domain.i18n;

public enum Lang {
    EN(true),
    FR(false),
    ES(false);

    private final boolean isDefault;

    Lang(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public static Lang fromCode(String langCode) {
        if (langCode == null) {
            return EN;
        }
        return switch (langCode.toLowerCase()) {
            case "fr" -> FR;
            case "es" -> ES;
            default -> EN;
        };
    }

    public String code() {
        return name();
    }
}
