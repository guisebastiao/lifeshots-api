package com.guisebastiao.lifeshotsapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UpdatePasswordRequest(

        @NotBlank(message = "Informe sua senha atual")
        @Length(min = 6, max = 20, message = "Sua senha atual deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "Sua senha atual deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "Sua senha atual deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "Sua senha atual deve conter pelo menos dois números"),
        })
        String currentPassword,

        @NotBlank(message = "Informe sua nova senha")
        @Length(min = 6, max = 20, message = "Sua nova senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "Sua nova senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "Sua nova senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "Sua nova senha deve conter pelo menos dois números"),
        })
        String newPassword,

        @NotBlank(message = "Confirme sua nova senha")
        @Length(min = 6, max = 20, message = "Sua nova senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "Sua nova senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "Sua nova senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "Sua nova senha deve conter pelo menos dois números"),
        })
        String confirmPassword
) { }
