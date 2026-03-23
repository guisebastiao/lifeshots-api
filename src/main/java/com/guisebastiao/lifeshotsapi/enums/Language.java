package com.guisebastiao.lifeshotsapi.enums;

import java.util.Locale;

public enum Language {
    PT_BR(new Locale("pt", "BR")),
    EN_US(Locale.US);

    private final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
