package com.lifeshots.lifeshotsapi.mappers;

import com.lifeshots.lifeshotsapi.dtos.NotificationDTO;
import com.lifeshots.lifeshotsapi.dtos.response.NotificationResponseDTO;
import com.lifeshots.lifeshotsapi.models.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notification);
    NotificationResponseDTO toResponseDTO(Notification notification);
}
