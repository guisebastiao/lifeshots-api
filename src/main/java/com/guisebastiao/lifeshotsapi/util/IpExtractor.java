package com.guisebastiao.lifeshotsapi.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public final class IpExtractor {

    private static final List<String> IP_HEADERS = List.of("X-Forwarded-For", "X-Real-IP");

    private IpExtractor() {}

    public static String extract(HttpServletRequest request) {
        return IP_HEADERS.stream()
                .map(request::getHeader)
                .filter(h -> h != null && !h.isBlank())
                .map(h -> h.split(",")[0].trim())
                .findFirst()
                .orElse(request.getRemoteAddr());
    }
}
