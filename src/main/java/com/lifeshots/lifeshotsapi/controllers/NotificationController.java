package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<DefaultDTO> findAllNotifications(@RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "20") int limit) {
        DefaultDTO response = this.notificationService.findAllNotifications(offset, limit);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
