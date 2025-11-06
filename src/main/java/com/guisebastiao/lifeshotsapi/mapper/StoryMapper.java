package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.entity.Story;
import com.guisebastiao.lifeshotsapi.mapper.resolver.StoryResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { StoryResolver.class, ProfileMapper.class, StoryPictureMapper.class })
public interface StoryMapper {
    Story toEntity(StoryRequest dto);

    @Mapping(target = "isOwner", source = ".", qualifiedByName = "resolveIsOwner")
    @Mapping(target = "isLiked", source = ".", qualifiedByName = "resolveIsLiked")
    StoryResponse toDTO(Story entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStory(StoryRequest request, @MappingTarget Story entity);
}
