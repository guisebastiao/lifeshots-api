package com.lifeshots.lifeshotsapi.repositories;

import com.lifeshots.lifeshotsapi.models.Follow;
import com.lifeshots.lifeshotsapi.models.FollowPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowPk> {
}
