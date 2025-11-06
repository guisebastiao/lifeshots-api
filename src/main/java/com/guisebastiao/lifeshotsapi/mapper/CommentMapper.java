package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.CommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.CommentResponse;
import com.guisebastiao.lifeshotsapi.entity.Comment;
import com.guisebastiao.lifeshotsapi.mapper.resolver.CommentResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, CommentResolver.class })
public interface CommentMapper {
    Comment toEntity(CommentRequest dto);

    @Mapping(target = "isOwner", source = ".", qualifiedByName = "resolveIsOwner")
    @Mapping(target = "isLiked", source = ".", qualifiedByName = "resolveIsLiked")
    CommentResponse toDTO(Comment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateComment(CommentRequest request, @MappingTarget Comment entity);
}
