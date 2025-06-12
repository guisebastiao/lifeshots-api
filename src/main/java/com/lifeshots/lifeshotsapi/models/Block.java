package com.lifeshots.lifeshotsapi.models;

import com.lifeshots.lifeshotsapi.utils.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "blocks")
public class Block extends Auditable {

    @EmbeddedId
    private BlockPk id;

    @ManyToOne
    @MapsId("blockerId")
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne
    @MapsId("blockedId")
    @JoinColumn(name = "blocked_id")
    private User blocked;
}
