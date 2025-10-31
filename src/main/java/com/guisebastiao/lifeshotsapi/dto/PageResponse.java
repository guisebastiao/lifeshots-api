package com.guisebastiao.lifeshotsapi.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        Paging paging
) { }
