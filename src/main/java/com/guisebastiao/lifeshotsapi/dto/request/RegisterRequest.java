package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.validator.passwordMatcher.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@PasswordMatches
public record RegisterRequest(
        @NotBlank(message = "Informe seu nome de usuário")
        @Length(min = 3, max = 20, message = "Seu nome de usuário tem que possuir entre 3 a 20 caracteres")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9._]*$",
                message = "O nome de usuário deve começar com uma letra e conter apenas letras, números, pontos ou underlines, sem espaços"
        )
        String handle,

        @NotBlank(message = "Informe seu email")
        @Email(message = "Informe um email válido")
        @Length(max = 250, message = "O email tem que possuir no máximo 250 caracteres")
        String email,

        @NotBlank(message = "Informe sua senha")
        @Length(min = 6, max = 20, message = "Sua senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "Sua senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "Sua senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "Sua senha deve conter pelo menos dois números"),
        })
        String password,

        @NotBlank(message = "Confirme sua senha")
        @Length(min = 6, max = 20, message = "A senha deve ter entre 6 a 20 caracteres")
        @Pattern.List({
                @Pattern(regexp = ".*[A-Z].*", message = "A senha deve conter pelo menos uma letra maiúscula"),
                @Pattern(regexp = ".*[@#$%&*!].*", message = "A senha deve conter pelo menos um caractere especial"),
                @Pattern(regexp = "(?:.*\\d){2,}.*", message = "A senha deve conter pelo menos dois números"),
        })
        String confirmPassword
) { }
