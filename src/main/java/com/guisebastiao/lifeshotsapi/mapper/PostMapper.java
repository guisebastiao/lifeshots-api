package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.PostRequest;
import com.guisebastiao.lifeshotsapi.dto.request.PostUpdateRequest;
import com.guisebastiao.lifeshotsapi.dto.response.PostResponse;
import com.guisebastiao.lifeshotsapi.entity.Post;
import com.guisebastiao.lifeshotsapi.mapper.resolver.PostResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { PostResolver.class, ProfileMapper.class, PostPictureMapper.class })
public interface PostMapper {
    Post toEntity(PostRequest dto);

    @Mapping(target = "isOwner", source = ".", qualifiedByName = "resolveIsOwner")
    @Mapping(target = "isLiked", source = ".", qualifiedByName = "resolveIsLiked")
    PostResponse toDTO(Post entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "postPictures", ignore = true)
    void updatePost(PostUpdateRequest request, @MappingTarget Post entity);
}
