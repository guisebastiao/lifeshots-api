package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.NotificationSettingControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationSettingRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationSettingResponse;
import com.guisebastiao.lifeshotsapi.service.NotificationSettingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification-setting")
public class NotificationSettingController implements NotificationSettingControllerDocs {

    private final NotificationSettingService notificationSettingService;

    public NotificationSettingController(NotificationSettingService notificationSettingService) {
        this.notificationSettingService = notificationSettingService;
    }

    @PostMapping("/disable-all")
    public ResponseEntity<DefaultResponse<NotificationSettingResponse>> disableAllNotifications() {
        DefaultResponse<NotificationSettingResponse> response = this.notificationSettingService.disableAllNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/enable-all")
    public ResponseEntity<DefaultResponse<NotificationSettingResponse>> enableAllNotifications() {
        DefaultResponse<NotificationSettingResponse> response = this.notificationSettingService.enableAllNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<DefaultResponse<NotificationSettingResponse>> findNotificationSetting() {
        DefaultResponse<NotificationSettingResponse> response = this.notificationSettingService.findNotificationSetting();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping
    public ResponseEntity<DefaultResponse<NotificationSettingResponse>> updateNotificationSetting(@RequestBody @Valid NotificationSettingRequest dto) {
        DefaultResponse<NotificationSettingResponse> response = this.notificationSettingService.updateNotificationSetting(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
