package com.guisebastiao.lifeshotsapi.dto;

public record MailDTO(
        String to,
        String subject,
        String template
) { }
