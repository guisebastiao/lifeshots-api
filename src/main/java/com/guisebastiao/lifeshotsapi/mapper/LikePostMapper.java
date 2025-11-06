package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.LikePostResponse;
import com.guisebastiao.lifeshotsapi.entity.LikePost;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class })
public interface LikePostMapper {
    LikePostResponse toDTO(LikePost entity);

}
