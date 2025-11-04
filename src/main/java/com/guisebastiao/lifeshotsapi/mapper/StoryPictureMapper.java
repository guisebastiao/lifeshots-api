package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.StoryPictureResponse;
import com.guisebastiao.lifeshotsapi.entity.StoryPicture;
import com.guisebastiao.lifeshotsapi.mapper.resolver.StoryPictureResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { StoryPictureResolver.class })
public interface StoryPictureMapper {
    @Mapping(target = "url", source = ".", qualifiedByName = "getStoryPictureUrl")
    StoryPictureResponse toDTO(StoryPicture entity);
}
