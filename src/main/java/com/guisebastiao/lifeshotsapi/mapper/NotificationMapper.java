package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})
public interface NotificationMapper {
    NotificationResponse toDTO(Notification notification);
}
