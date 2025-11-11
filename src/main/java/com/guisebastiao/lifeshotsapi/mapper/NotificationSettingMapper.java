package com.guisebastiao.lifeshotsapi.mapper;

import com.guisebastiao.lifeshotsapi.dto.request.NotificationSettingRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationSettingResponse;
import com.guisebastiao.lifeshotsapi.entity.NotificationSetting;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface NotificationSettingMapper {
    NotificationSettingResponse toDTO(NotificationSetting notificationSetting);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationSetting(NotificationSettingRequest request, @MappingTarget NotificationSetting entity);
}
