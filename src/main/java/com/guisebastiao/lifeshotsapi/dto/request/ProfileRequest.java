package com.guisebastiao.lifeshotsapi.dto.request;

import org.hibernate.validator.constraints.Length;

public record ProfileRequest(
        @Length(min = 3, max = 250, message = "{validation.profile-request.full-name.length}")
        String fullName,

        @Length(max = 300, message = "{validation.profile-request.bio.length}")
        String bio
) { }
