package com.guisebastiao.lifeshotsapi.controller;

import com.guisebastiao.lifeshotsapi.controller.docs.PushSubscriptionControllerDocs;
import com.guisebastiao.lifeshotsapi.dto.DefaultResponse;
import com.guisebastiao.lifeshotsapi.dto.request.PushSubscriptionRequest;
import com.guisebastiao.lifeshotsapi.service.PushSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/push")
public class PushSubscriptionController implements PushSubscriptionControllerDocs {

    private final PushSubscriptionService pushSubscriptionService;

    public PushSubscriptionController(PushSubscriptionService pushSubscriptionService) {
        this.pushSubscriptionService = pushSubscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<DefaultResponse<Void>> subscribe(@RequestBody PushSubscriptionRequest dto) {
        DefaultResponse<Void> response = pushSubscriptionService.saveSubscription(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
