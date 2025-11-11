package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.ReplyCommentRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ReplyCommentResponse;
import com.guisebastiao.lifeshotsapi.entity.ReplyComment;
import com.guisebastiao.lifeshotsapi.mapper.resolver.ReplyCommentResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { ProfileMapper.class, ReplyCommentResolver.class })
public interface ReplyCommentMapper {
    ReplyComment toEntity(ReplyCommentRequest dto);

    @Mapping(target = "isOwner", source = ".", qualifiedByName = "resolveIsOwner")
    @Mapping(target = "isLiked", source = ".", qualifiedByName = "resolveIsLiked")
    ReplyCommentResponse toDTO(ReplyComment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReplyComment(ReplyCommentRequest request, @MappingTarget ReplyComment entity);
}
