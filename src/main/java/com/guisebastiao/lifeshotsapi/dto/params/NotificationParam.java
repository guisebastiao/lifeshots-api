package com.guisebastiao.lifeshotsapi.dto.params;

import com.guisebastiao.lifeshotsapi.enums.ReadType;
import com.guisebastiao.lifeshotsapi.validator.validateEnum.EnumValidator;

public record NotificationParam(

        @EnumValidator(enumClass = ReadType.class)
        String filter
) { }
