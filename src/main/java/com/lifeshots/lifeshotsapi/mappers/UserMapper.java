package com.lifeshots.lifeshotsapi.mappers;

import com.lifeshots.lifeshotsapi.dtos.request.RegisterRequestDTO;
import com.lifeshots.lifeshotsapi.dtos.response.UserResponseDTO;
import com.lifeshots.lifeshotsapi.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProfilePictureMapper.class})
public interface UserMapper {
    UserResponseDTO toDTO(User user);
    User toEntity(RegisterRequestDTO registerRequestDTO);
}
