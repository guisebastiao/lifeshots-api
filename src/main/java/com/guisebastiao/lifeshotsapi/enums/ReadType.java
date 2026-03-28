package com.guisebastiao.lifeshotsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReadType {
    READ("READ"),
    UNREAD("UNREAD");

    private final String value;
}