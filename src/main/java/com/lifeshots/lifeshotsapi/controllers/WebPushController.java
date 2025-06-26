package com.lifeshots.lifeshotsapi.controllers;

import com.lifeshots.lifeshotsapi.dtos.DefaultDTO;
import com.lifeshots.lifeshotsapi.dtos.request.SubscribeWebPushRequestDTO;
import com.lifeshots.lifeshotsapi.services.WebPushService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webpush")
public class WebPushController {

    @Autowired
    private WebPushService webPushService;

    @PostMapping("/subscribe")
    public ResponseEntity<DefaultDTO> subscribe(@RequestBody @Valid SubscribeWebPushRequestDTO subscribeWebPushRequestDTO) {
        DefaultDTO response = webPushService.subscribe(subscribeWebPushRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
