package com.guisebastiao.lifeshotsapi.dto.request;

import com.guisebastiao.lifeshotsapi.enums.Language;
import com.guisebastiao.lifeshotsapi.validator.validateEnum.EnumValidator;
import jakarta.validation.constraints.NotNull;

public record LanguageRequest(

        @NotNull
        @EnumValidator(enumClass = Language.class)
        String language
) { }
