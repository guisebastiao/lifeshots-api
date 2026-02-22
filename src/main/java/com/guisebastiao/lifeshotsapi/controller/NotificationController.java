package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.NotificationControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.params.NotificationParam;
import com.guisebastiao.lifeshotsapi.dto.params.PaginationParam;
import com.guisebastiao.lifeshotsapi.dto.request.DeleteRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.dto.response.UnreadResponse;
import com.guisebastiao.lifeshotsapi.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController implements NotificationControllerDocs {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<DefaultResponse<List<NotificationResponse>>> findAllNotificationsByUser(NotificationParam param, @Valid PaginationParam pagination) {
        DefaultResponse<List<NotificationResponse>> data = notificationService.findAllNotificationsByUser(param, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<DefaultResponse<UnreadResponse>> findUnreadNotificationsByUser() {
        DefaultResponse<UnreadResponse> data = notificationService.findUnreadNotificationsByUser();
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<DefaultResponse<NotificationResponse>> findNotificationById(@PathVariable String notificationId) {
        DefaultResponse<NotificationResponse> data = notificationService.findNotificationById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<DefaultResponse<Void>> readNotificationById(@PathVariable String notificationId) {
        DefaultResponse<Void> data = notificationService.readNotificationById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @PatchMapping("/read-all")
    public ResponseEntity<DefaultResponse<Void>> readAllUnreadNotifications() {
        DefaultResponse<Void> data = notificationService.readAllUnreadNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<DefaultResponse<Void>> deleteNotificationById(@PathVariable String notificationId) {
        DefaultResponse<Void> data = notificationService.deleteNotificationById(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<Void>> deleteNotifications(@RequestBody @Valid DeleteRequest dto) {
        DefaultResponse<Void> data = notificationService.deleteNotifications(dto);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
