package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.ProfileRequest;
import com.guisebastiao.lifeshotsapi.dto.response.ProfileResponse;
import com.guisebastiao.lifeshotsapi.entity.Profile;
import com.guisebastiao.lifeshotsapi.mapper.resolver.ProfileResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ProfileResolver.class})
public interface ProfileMapper {

    @Mapping(target = "handle", source = ".", qualifiedByName = "resolveGetHandle")
    @Mapping(target = "isOwnProfile", source = ".", qualifiedByName = "resolveIsOwnProfile")
    ProfileResponse toDTO(Profile entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(ProfileRequest request, @MappingTarget Profile entity);
}
