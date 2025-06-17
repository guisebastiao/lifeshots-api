package com.lifeshots.lifeshotsapi.mappers;

import com.lifeshots.lifeshotsapi.dtos.response.ProfilePictureResponseDTO;
import com.lifeshots.lifeshotsapi.mappers.resolvers.ProfilePictureResolver;
import com.lifeshots.lifeshotsapi.models.ProfilePicture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfilePictureResolver.class})
public interface ProfilePictureMapper {
    @Mapping(target = "url", source = ".", qualifiedByName = "getProfilePicture")
    ProfilePictureResponseDTO toDTO(ProfilePicture profilePicture);
}
