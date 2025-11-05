package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.LikeStoryResponse;
import com.guisebastiao.lifeshotsapi.entity.LikeStory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class })
public interface LikeStoryMapper {
    LikeStoryResponse toDTO(LikeStory entity);
}
