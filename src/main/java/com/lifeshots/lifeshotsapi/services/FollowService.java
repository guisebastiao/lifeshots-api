package com.lifeshots.lifeshotsapi.services;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.FollowRequestDTO;

public interface FollowService {
    DefaultDTO follow(FollowRequestDTO followRequestDTO);
    DefaultDTO unfollow(FollowRequestDTO followRequestDTO);
}
