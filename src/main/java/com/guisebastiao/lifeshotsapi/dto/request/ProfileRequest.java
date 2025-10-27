package com.guisebastiao.lifeshotsapi.dto.request;

import org.hibernate.validator.constraints.Length;

public record ProfileRequest(
        @Length(min = 3, max = 250, message = "Seu nome completo tem que possuir menos de 250 caracteres")
        String fullName,

        @Length(max = 300, message = "Sua biografia tem que possuir menos de 300 caracteres")
        String bio
) { }
