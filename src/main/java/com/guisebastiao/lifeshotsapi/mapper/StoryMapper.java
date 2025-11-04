package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.StoryRequest;
import com.guisebastiao.lifeshotsapi.dto.response.StoryResponse;
import com.guisebastiao.lifeshotsapi.entity.Story;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, StoryPictureMapper.class })
public interface StoryMapper {
    Story toEntity(StoryRequest dto);
    StoryResponse toDTO(Story entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStory(StoryRequest request, @MappingTarget Story entity);
}
