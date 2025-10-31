package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.FollowResponse;
import com.guisebastiao.lifeshotsapi.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface FollowMapper {
    FollowResponse toDTO(Follow entity);

    @Mapping(target = "profile", source = "follower")
    FollowResponse toFollowerDTO(Follow entity);

    @Mapping(target = "profile", source = "following")
    FollowResponse toFollowingDTO(Follow entity);
}
