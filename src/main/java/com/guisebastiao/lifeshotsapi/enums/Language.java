package com.guisebastiao.lifeshotsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@Getter
@AllArgsConstructor
public enum Language {
    PT_BR(new Locale("pt", "BR")),
    EN_US(Locale.US);

    private final Locale locale;
}
