package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.PostPictureResponse;
import com.guisebastiao.lifeshotsapi.entity.PostPicture;
import com.guisebastiao.lifeshotsapi.mapper.resolver.PostPictureResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { PostPictureResolver.class })
public interface PostPictureMapper {

    @Mapping(target = "url", source = ".", qualifiedByName = "getPostPictureUrl")
    PostPictureResponse toDTO(PostPicture entity);
}
