package com.guisebastiao.lifeshotsapi.dto.params;

import com.guisebastiao.lifeshotsapi.enums.FollowType;
import com.guisebastiao.lifeshotsapi.validator.validateEnum.EnumValidator;
import jakarta.validation.constraints.NotNull;

public record FollowParam(

        @NotNull
        @EnumValidator(enumClass = FollowType.class)
        String type
) { }
