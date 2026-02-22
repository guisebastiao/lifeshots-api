package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.RegisterRequest;
import com.guisebastiao.lifeshotsapi.dto.response.AuthResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UserResponse;
import com.guisebastiao.lifeshotsapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserResponse toDTO(User entity);
    User toEntity(RegisterRequest dto);
    AuthResponse authDTO(User entity);
}
