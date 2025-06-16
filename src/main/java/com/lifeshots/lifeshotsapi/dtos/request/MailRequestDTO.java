package com.lifeshots.lifeshotsapi.dtos.request;

public record MailRequestDTO(
        String to,
        String subject,
        String template
) { }
