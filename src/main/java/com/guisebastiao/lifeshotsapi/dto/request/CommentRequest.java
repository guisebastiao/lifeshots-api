package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CommentRequest(
        @NotBlank(message = "Informe seu comentário")
        @Length(max = 300, message = "O comentário tem que possuir menos de 300 caracteres")
        String content
) { }
