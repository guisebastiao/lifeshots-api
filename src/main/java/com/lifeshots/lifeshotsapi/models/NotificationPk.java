package com.lifeshots.lifeshotsapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class NotificationPk {

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;
}
