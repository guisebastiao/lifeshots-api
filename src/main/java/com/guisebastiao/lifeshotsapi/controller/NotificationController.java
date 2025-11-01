package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.PageResponse;
import com.guisebastiao.lifeshotsapi.dto.PaginationFilter;
import com.guisebastiao.lifeshotsapi.dto.request.NotificationRequest;
import com.guisebastiao.lifeshotsapi.dto.response.NotificationResponse;
import com.guisebastiao.lifeshotsapi.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<DefaultResponse<PageResponse<NotificationResponse>>> findAllNotifications(@Valid PaginationFilter pagination) {
        DefaultResponse<PageResponse<NotificationResponse>> response = this.notificationService.findAllNotifications(pagination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping
    public ResponseEntity<DefaultResponse<List<NotificationResponse>>> updatedReadNotifications() {
        DefaultResponse<List<NotificationResponse>> response = this.notificationService.updatedReadNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<DefaultResponse<Void>> deleteNotification(@PathVariable String notificationId) {
        DefaultResponse<Void> response = this.notificationService.deleteNotification(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<Void>> deleteNotifications(@RequestBody @Valid NotificationRequest dto) {
        DefaultResponse<Void> response = this.notificationService.deleteNotifications(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
