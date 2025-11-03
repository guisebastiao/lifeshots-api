package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.ProfilePictureResponse;
import com.guisebastiao.lifeshotsapi.entity.ProfilePicture;
import com.guisebastiao.lifeshotsapi.mapper.resolver.ProfilePictureResolver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfilePictureResolver.class})
public interface ProfilePictureMapper {

    @Mapping(target = "url", source = ".", qualifiedByName = "getProfilePictureUrl")
    ProfilePictureResponse toDTO(ProfilePicture entity);
}
