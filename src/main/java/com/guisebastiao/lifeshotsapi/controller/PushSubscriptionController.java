package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
public class PushSubscriptionController {

    @Autowired
    private PushSubscriptionService pushSubscriptionService;

    @PostMapping("/subscribe")
    public ResponseEntity<DefaultResponse<Void>> subscribe(@RequestBody PushSubscriptionRequest dto) {
        DefaultResponse<Void> response = pushSubscriptionService.saveSubscription(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<DefaultResponse<Void>> unsubscribe() {
        DefaultResponse<Void> response = this.pushSubscriptionService.removeSubscription();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
