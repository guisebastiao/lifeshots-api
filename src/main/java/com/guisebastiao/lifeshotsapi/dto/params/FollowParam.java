package com.guisebastiao.lifeshotsapi.dto.params;

import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.validator.validateEnum.EnumValidator;
import jakarta.validation.constraints.NotNull;

public record FollowParam(

        @NotNull(message = "{validation.follow-param.type.not-null}")
        @EnumValidator(enumClass = FollowType.class, message = "{validation.follow-param.type.invalid-enum}")
        String type
) { }
