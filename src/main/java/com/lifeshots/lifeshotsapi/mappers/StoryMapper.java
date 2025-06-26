package com.lifeshots.lifeshotsapi.mappers;

import com.lifeshots.lifeshotsapi.dtos.request.StoryCreateRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.response.StoryResponseDTO;
import com.lifeshots.lifeshotsapi.mappers.resolvers.StoryResolver;
import com.lifeshots.lifeshotsapi.models.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StoryResolver.class, UserMapper.class})
public interface StoryMapper {
    @Mapping(target = "storyPicture", source = ".", qualifiedByName = "getStoryPicture")
    StoryResponseDTO toDTO(Story story);
    Story toEntity(StoryCreateRequestDTO storyCreateRequestDTO);
}
