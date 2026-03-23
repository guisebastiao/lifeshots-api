package com.guisebastiao.lifeshotsapi.dto.request;

import org.hibernate.validator.constraints.Length;

public record ProfileRequest(
        @Length(min = 3, max = 250)
        String fullName,

        @Length(max = 300)
        String bio
) { }
